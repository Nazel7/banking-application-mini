package com.decagon.bank.dtos.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignUpDto {
    // This is typical account signUp model for real life scenerio.
    // Due to the limitation of this test it will be limited to accountName and phone
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String phone;
    private String homeAddress;
    private String accountType;
    private String pin;
    private String authority;
    private String bvn;
    private String verificationCode;
    private Boolean verifiedEmail;
    private Boolean verifiedPhone;
    private Boolean verifiedBvn;
    private Boolean verifiedHomeAddress;

}
