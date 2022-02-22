package com.ripple.provisionvm.service;

import java.util.List;
import java.util.regex.Pattern;

import com.ripple.provisionvm.common.AuthUtils;
import com.ripple.provisionvm.common.SignupRequest;
import com.ripple.provisionvm.model.AppUser;
import com.ripple.provisionvm.repositories.AppUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AccountService{

    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    AuthUtils authUtils;

    @Transactional
    public int addNewAppUser(SignupRequest signupRequest)throws RuntimeException{
        String emailId = signupRequest.getEmailId();
        String mobileNumber = signupRequest.getMobileNumber();
        String password = signupRequest.getPassword();
        String role = signupRequest.getRole();
        String name = signupRequest.getName();
        
        AppUser appUser = new AppUser();
        appUser.setName(name);
        appUser.setEmailId(emailId);
        appUser.setMobileNumber(mobileNumber);
        appUser.setRole(role);
        appUser.setPassword(authUtils.encoder().encode(password));
        int userId = appUserRepository.save(appUser).getUserId();
        log.info(String.format("New App User with id %s created with role %s", userId, signupRequest.getRole()));
        return userId;
    }

    public int getUserId(String username)throws RuntimeException{
        int userId = -1;
        if(appUserRepository.findByUsername(username).isPresent()){
            userId = appUserRepository.findByUsername(username).get().getUserId();
            log.info(String.format("Found user with id %s corresponding to username %s", userId, username));
        }
        else{
            throw new RuntimeException("No user with email or mobile number as "+ username+ "exists.");
        }
        return userId;
    }

    @Transactional
    public void deleteAppUser(int userId)throws RuntimeException{

        appUserRepository.findById(userId).ifPresentOrElse(user -> {
            log.info(String.format("Deleting user with user id ", userId));
            appUserRepository.delete(user);
        },
        ()->{
            throw new RuntimeException("No user with user id "+ userId+ "exists.");
        });

        return;
    }

    public List<AppUser> getAllUsers(){
        List<AppUser>userList = appUserRepository.findAll();
        userList.forEach(user ->{ user.setPassword(null);});
        return userList;
        
    }
    public boolean isValidSignupRequest(SignupRequest request){
        // could include validation for role
        if(request.getEmailId() == null && request.getMobileNumber() == null){
            log.error("No unique field provided. Either a valid email Id or mobile Number is required to create a new user");
            return false;
        }
        if(!isValidEmailId(request.getEmailId()) || !isValidMobileNumber(request.getMobileNumber())){
            log.error("No unique field provided. Either a valid email Id or mobile Number is required to create a new user");
            return false;
        }
        return isValidPassword(request.getPassword());
    }
    private boolean isValidEmailId(String emailId){
        if(emailId!=null && !Pattern.matches("^(.+)@(\\S+)$", emailId)){
            log.error("Invalid email Id");
            return false;
        };
        return true;
    }

    private boolean isValidMobileNumber(String mobileNumber){
        if(mobileNumber!=null && !Pattern.matches("[1-9][0-9]{9}", mobileNumber)){
            log.error("Invalid mobile number");
            return false;
        };
        return true;
    }

    private boolean isValidPassword(String password){
        if(password == null || password.length() < 8){
            log.error("Password is not of sufficient length. Must be at least 8 characters long");
            return false;
        }
        return true;
    }
    
}