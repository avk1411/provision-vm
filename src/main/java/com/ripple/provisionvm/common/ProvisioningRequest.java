package com.ripple.provisionvm.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProvisioningRequest {
    String operatingSystem;
    String ram;
    String hardDisk;
    int cores;
}
