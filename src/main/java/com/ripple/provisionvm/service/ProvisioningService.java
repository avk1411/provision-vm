package com.ripple.provisionvm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ripple.provisionvm.common.ProvisioningRequest;
import com.ripple.provisionvm.model.ProvisionedVm;
import com.ripple.provisionvm.repositories.ProvisionedVmRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class ProvisioningService{
    @Autowired
    ProvisionedVmRepository provisionedVmRepository;

    List<String> operatingSystemLookup = new ArrayList<>(
        Arrays.asList("Windows", "Linux","Mac")
    );
    List<String> ramLookup = new ArrayList<>(
        Arrays.asList("1GB","2GB","4GB","8GB","16GB","32GB")
    );

    List<String> harddiskLookup = new ArrayList<>(
        Arrays.asList("500GB", "1TB","2TB","5TB")
    );

    @Transactional
    public int provisionNewVM(int userId, ProvisioningRequest provisioningRequest){
        ProvisionedVm vm = new ProvisionedVm();
        vm.setUserId(userId);
        vm.setRam(provisioningRequest.getRam());
        vm.setCores(provisioningRequest.getCores());
        vm.setOperatingSystem(provisioningRequest.getOperatingSystem());
        String harddisk = provisioningRequest.getHardDisk();
        if(harddisk.equals("500GB")){
            harddisk = "0.5TB";
        }
        vm.setHardDisk(harddisk);

        int vmId = provisionedVmRepository.save(vm).getVmId();
        log.info(String.format("New VM with id %s provisioned to user id %s", vmId,userId));
        
        return vmId;
    }

    public List<ProvisionedVm> getAllVMByUserId(int userId){
        log.info(String.format("Getting all VMs for userId",userId));
        List<ProvisionedVm> result = new ArrayList<>();
        result = provisionedVmRepository.findByUserId(userId);
        return result;
    }
    public List<ProvisionedVm> getAllVMs(){
        List<ProvisionedVm> result = new ArrayList<>();
        result = provisionedVmRepository.findAll();
        return result;
    }

    public List<ProvisionedVm> getTopVMByMemory(int userId, int n){
        log.info(String.format("Getting top %s VM for userId", n , userId));
        List<ProvisionedVm> vmList = new ArrayList<>();
        vmList = provisionedVmRepository.findByUserId(userId, Sort.by("hardDisk").descending());
        List<ProvisionedVm> result= new ArrayList<>();
        if(vmList != null){
            result = (n <= vmList.size()) ? vmList.subList(0, n): vmList;
        } 
        return result;
    }

    public List<ProvisionedVm> getAllTopVMByMemory(int n){
        List<ProvisionedVm> vmList =  provisionedVmRepository.findAll(Sort.by("hardDisk").descending());
        List<ProvisionedVm> result= new ArrayList<>();
        if(vmList != null){
            result =  (n <= vmList.size()) ? vmList.subList(0, n): vmList;
        } 
        return result;
    }

    public boolean isValidProvisioningRequest(ProvisioningRequest request){
        //these would ideally be database lookups
        boolean isValid = true;
    
        if(!ramLookup.contains(request.getRam())){
            log.error("Invalid RAM format in provisioning request");
            isValid =false;
        }
        if(!harddiskLookup.contains(request.getHardDisk())){
            log.error("Invalid Hard disk format in provisioning request");
            isValid =false;
        }
        if(!operatingSystemLookup.contains(request.getOperatingSystem())){
            log.error("Invalid Operating System format in provisioning request");
            isValid =false;
        }
        return isValid;
    }
}