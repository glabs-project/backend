package com.glabs.commonRepositories;

import com.glabs.models.EmailVerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailVerificationTokenRepository extends MongoRepository<EmailVerificationToken, String> {
    EmailVerificationToken findByEmailAndToken(String email, String token);
    EmailVerificationToken findByEmail(String email);
}
