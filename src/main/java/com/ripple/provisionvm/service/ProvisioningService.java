package com.ripple.provisionvm.service;

import java.util.ArrayList;
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

    @Transactional
    public int provisionNewVM(int userId, ProvisioningRequest provisioningRequest){
        ProvisionedVm vm = new ProvisionedVm();
        vm.setUserId(userId);
        vm.setRam(provisioningRequest.getRam());
        vm.setCores(provisioningRequest.getCores());
        vm.setOperatingSystem(provisioningRequest.getOperatingSystem());
        vm.setHardDisk(provisioningRequest.getHardDisk());

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
        vmList = provisionedVmRepository.findByUserId(userId, Sort.by("hard_disk").descending());
        List<ProvisionedVm> result = vmList.subList(0, n);
        return result;
    }

    public List<ProvisionedVm> getAllTopVMByMemory(int n){
        return provisionedVmRepository.findAll(Sort.by("hard_disk").descending()).subList(0, n);
    }
}