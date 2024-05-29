package com.glabs.entities.user.services;


import com.glabs.commonRepositories.EmailVerificationTokenRepository;
import com.glabs.commonService.EmailService;
import com.glabs.models.EmailVerificationToken;
import com.glabs.scripts.VerificationCodeGenerator;
import com.glabs.shared.ERole;
import com.glabs.models.Role;
import com.glabs.models.User;
import com.glabs.payload.request.LoginRequest;
import com.glabs.payload.request.SignUpRequest;
import com.glabs.payload.request.UpdateUserRequest;
import com.glabs.payload.response.JwtResponse;
import com.glabs.payload.response.MessageResponse;
import com.glabs.commonRepositories.RoleRepository;
import com.glabs.entities.user.repository.UserRepository;
import com.glabs.security.jwt.JwtUtils;
import com.glabs.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Value("${email.confirm.timer.minutes}")
    private Integer timerForCode;

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;

    public ResponseEntity<?> createUser(SignUpRequest signUpRequest,
                                        BindingResult bindingResult,
                                        UriComponentsBuilder uriComponentsBuilder) throws BindException {

        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        }

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFirstName() != null ? signUpRequest.getFirstName() : null,
                signUpRequest.getLastName() != null ? signUpRequest.getLastName() : null,
                signUpRequest.getPhoneNumber() != null ? signUpRequest.getPhoneNumber() : null);

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        String code = VerificationCodeGenerator.generateCode();
        EmailVerificationToken token = new EmailVerificationToken();
        token.setEmail(user.getEmail());
        token.setToken(code);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(timerForCode));
        emailVerificationTokenRepository.save(token);
        emailService.sendVerificationCode(user.getEmail(), code);


        return ResponseEntity.ok().body(new MessageResponse("code send"));
    }

    public ResponseEntity<?> confirmEmailToken(String email, String code, UriComponentsBuilder uriComponentsBuilder) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByEmailAndToken(email, code);
        if (emailVerificationToken == null) {
            return ResponseEntity.notFound().build();
        }

        if (emailVerificationToken.getExpiryDate().isAfter(LocalDateTime.now())) {
            emailVerificationTokenRepository.delete(emailVerificationToken);
            User user = userRepository.findByEmail(email).get();
            user.setEnabled(true);
            userRepository.save(user);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> Roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/api/user")
                            .queryParam("id", user.getId())
                            .build().toUri())
                    .body(new JwtResponse(
                            jwt,
                            userDetails.getId(),
                            userDetails.getUsername(),
                            userDetails.getEmail(),
                            Roles));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<?> resendEmailConfirmCode(Map<String, Object> data) {
        String code = VerificationCodeGenerator.generateCode();
        EmailVerificationToken token = new EmailVerificationToken();
        token.setEmail((String) data.get("email"));
        token.setToken(code);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(timerForCode));
        emailVerificationTokenRepository.save(token);
        emailService.sendVerificationCode((String) data.get("email"), code);
        return ResponseEntity.ok().body(new MessageResponse("code send."));
    }

    public ResponseEntity<?> signIn(LoginRequest loginRequest,
                                    BindingResult bindingResult) throws BindException {

        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        }

        if (!userRepository.findByUsername(loginRequest.getUsername()).get().getEnabled()){
            return ResponseEntity.badRequest().body(new MessageResponse("not enabled account"));
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    public ResponseEntity<List<User>> getAll() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok().body(users);
    }

    public ResponseEntity<?> updateUser(String id, UpdateUserRequest updateUserRequest) {

        ResponseEntity<?> validationResponse = updateUserRequest.validateFields();
        if (validationResponse != null) {
            return validationResponse;
        }

        User user = userRepository.findById(id).get();

        BeanUtils.copyProperties(updateUserRequest, user, getNullPropertyNames(updateUserRequest));

        userRepository.save(user);

        return ResponseEntity.ok().body(user);
    }

    public ResponseEntity<?> deleteUser(String id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().body(new MessageResponse("id: " + id + " success delete"));
    }

    private String[] getNullPropertyNames(UpdateUserRequest source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : src.getPropertyDescriptors()) {
            if (src.getPropertyValue(pd.getName()) == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

}

