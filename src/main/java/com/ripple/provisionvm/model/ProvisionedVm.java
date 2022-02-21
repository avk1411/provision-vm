package com.ripple.provisionvm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProvisionedVm {
    @Id
    @GeneratedValue
    private int vmId;
    private int userId;
    private String operatingSystem;
    private String ram;
    private String hardDisk;
    private int cores;
}
