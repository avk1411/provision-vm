package com.ripple.provisionvm.service;

import java.util.List;

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
    public int addNewAppUser(SignupRequest signupRequest){
        AppUser appUser = new AppUser();
        appUser.setName(signupRequest.getName());
        appUser.setEmailId(signupRequest.getEmailId());
        appUser.setMobileNumber(signupRequest.getMobileNumber());
        appUser.setRole(signupRequest.getRole());
        appUser.setPassword(authUtils.encoder().encode(signupRequest.getPassword()));
        int userId = appUserRepository.save(appUser).getUserId();
        log.info(String.format("New App User with id %s created with role %s", userId, signupRequest.getRole()));
        return userId;
    }

    public int getUserId(String username){
        int userId = -1;
        userId = appUserRepository.findByUsername(username).get().getUserId();
        log.info(String.format("Found user with id %s corresponding to username %s", userId, username));
        return userId;
    }

    @Transactional
    public void deleteAppUser(int userId)throws RuntimeException{

        appUserRepository.findById(userId).ifPresentOrElse(user -> {
            appUserRepository.delete(user);
        },
        ()->{
            throw new RuntimeException("No user with id:"+ userId);
            
        });

        return;
    }

    public List<AppUser> getAllUsers(){
        List<AppUser>userList = appUserRepository.findAll();
        userList.forEach(user ->{ user.setPassword(null);});
        return userList;
        
    }
    
}