package com.ripple.provisionvm.controller;

import java.util.ArrayList;
import java.util.List;

import com.ripple.provisionvm.common.AuthUtils;
import com.ripple.provisionvm.common.ProvisioningRequest;
import com.ripple.provisionvm.model.ProvisionedVm;
import com.ripple.provisionvm.service.ProvisioningService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/vm")
public class ProvisioningController {
    @Autowired
    ProvisioningService provisioningService;

    @Autowired
    AuthUtils authUtils;

    @PostMapping("/provision")
    public String requestNewVM(WebRequest request, @RequestBody ProvisioningRequest provisioningRequest){
        if (provisioningService.isValidProvisioningRequest(provisioningRequest)) {
			int userId = authUtils.getUserIdFromRequest(request);
            int vmId = provisioningService.provisionNewVM(userId, provisioningRequest);
            return "New VM provisioned. VM Id: " + vmId; 
		}
		return "Invalid request body. Please provide accurate provisioning details";
    }

    @GetMapping("/getAllVMs")
    public List<ProvisionedVm> getRequestedVMList(WebRequest request){
        System.out.println(request.getUserPrincipal());
        if(request.isUserInRole("MASTER")){
            log.info("Access granted for MASTER role");
            return provisioningService.getAllVMs();
        }
        int userId = authUtils.getUserIdFromRequest(request);
        List<ProvisionedVm> result = provisioningService.getAllVMByUserId(userId);
        return result; 
    }

    @GetMapping("/getTopVMByMemory")
    public List<ProvisionedVm> getTopVMByMemory(WebRequest request, @RequestParam int n){
        if(n <= 0){
            return new ArrayList<>();
        }
        System.out.println(request.getUserPrincipal());
        if(request.isUserInRole("MASTER")){
            log.info("Access granted for MASTER role");
            return provisioningService.getAllTopVMByMemory(n);
        }
        
        int userId = authUtils.getUserIdFromRequest(request);
        return provisioningService.getTopVMByMemory(userId, n);
        
    }
}