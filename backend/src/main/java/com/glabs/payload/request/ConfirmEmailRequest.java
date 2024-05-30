package com.glabs.payload.request;

import lombok.Data;

@Data
public class ConfirmEmailRequest {
    private String code;
    private String email;
}
