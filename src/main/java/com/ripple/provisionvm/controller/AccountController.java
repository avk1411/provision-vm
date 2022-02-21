package com.ripple.provisionvm.controller;

import java.security.Principal;
import java.util.List;

import com.ripple.provisionvm.common.AuthUtils;
import com.ripple.provisionvm.common.SignupRequest;
import com.ripple.provisionvm.model.AppUser;
import com.ripple.provisionvm.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.log4j.Log4j2;


@Log4j2
@RestController
@RequestMapping("/account")
public class AccountController {
	@Autowired
	AccountService accountService;

	@Autowired
	AuthUtils authUtils;

	@GetMapping("/health")
	public String health(){
		return "OKAY";
	}
	@PostMapping("/signup")
	public String createNewUser(@RequestBody SignupRequest signupRequest) {
		int userId = accountService.addNewAppUser(signupRequest);
		return "New user with id "+ userId +"created.";   
	}

	@GetMapping("/authenticate")
	public String getJWTToken(Principal principal){
		int userId = accountService.getUserId(principal.getName());
		String jwt = authUtils.generateToken(userId);

		return jwt;
	}
	
	@DeleteMapping()
	public String deleteUserAccount(WebRequest request){
		int userId = authUtils.getUserIdFromRequest(request);
		accountService.deleteAppUser(userId);
		return "Deleted user successfully.";
	}
	@PreAuthorize("has_role('ROLE_MASTER')")
	@DeleteMapping("/{userId}")
	public String deleteUserAccount(@PathVariable int userId){
		log.info("Access granted for MASTER role");
		accountService.deleteAppUser(userId);
		return "Deleted user successfully.";
	}
	@PreAuthorize("has_role('ROLE_MASTER')")
	@GetMapping("/getUsers")
	public List<AppUser> getUsers(){
		log.info("Access granted for MASTER role");
		return accountService.getAllUsers();
	}

}