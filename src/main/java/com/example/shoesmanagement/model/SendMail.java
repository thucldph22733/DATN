package com.example.shoesmanagement.model;

import lombok.Data;

@Data
public class SendMail {
    private String name;
    private String email;
    private String subject;
    private String message;
}
