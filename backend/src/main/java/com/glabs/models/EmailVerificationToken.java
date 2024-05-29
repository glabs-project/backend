package com.glabs.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "verification_tokens")
@Data
public class EmailVerificationToken {

    @Id
    private String id;
    private String email;
    private String token;
    private LocalDateTime expiryDate;
}