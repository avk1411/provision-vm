package com.ripple.provisionvm.controller;

import java.util.List;

import com.ripple.provisionvm.common.AuthUtils;
import com.ripple.provisionvm.common.ProvisioningRequest;
import com.ripple.provisionvm.model.ProvisionedVm;
import com.ripple.provisionvm.service.ProvisioningService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping("/{userId}/provision")
    public String requestNewVM(@PathVariable int userId, @RequestBody ProvisioningRequest provisioningRequest){
        int vmId = provisioningService.provisionNewVM(userId, provisioningRequest);
        return "New VM provisioned. VM Id: " + vmId; 
    }
    
    @GetMapping("/getAllVMs")
    public List<ProvisionedVm> getRequestedVMList(WebRequest request){
        if(request.isUserInRole("ROLE_MASTER")){
            log.info("Access granted for MASTER role");
            return provisioningService.getAllVMs();
        }
        int userId = authUtils.getUserIdFromRequest(request);
        List<ProvisionedVm> result = provisioningService.getAllVMByUserId(userId);
        return result; 
    }

    @GetMapping("/TopVMByMemory")
    public List<ProvisionedVm> getTopVMByMemory(WebRequest request, @RequestParam int n){
        if(request.isUserInRole("ROLE_MASTER")){
            log.info("Access granted for MASTER role");
            return provisioningService.getAllTopVMByMemory(n);
        }
        
        int userId = authUtils.getUserIdFromRequest(request);
        return provisioningService.getTopVMByMemory(userId, n);
        
    }
}