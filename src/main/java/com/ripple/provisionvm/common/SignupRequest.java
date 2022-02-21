package com.ripple.provisionvm.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest{
    String name;
    String emailId;
    String mobileNumber;
    String password;
    String role;
}