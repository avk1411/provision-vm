package com.ripple.provisionvm.repositories;

import java.util.List;

import com.ripple.provisionvm.model.ProvisionedVm;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvisionedVmRepository extends JpaRepository<ProvisionedVm,Integer> {
    public List<ProvisionedVm> findByUserId(int userId);

    public List<ProvisionedVm> findByUserId(int userId, Sort sort);

    public void deleteByUserId(int userId);
}
