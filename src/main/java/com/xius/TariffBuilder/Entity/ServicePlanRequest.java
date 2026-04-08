package com.xius.TariffBuilder.Entity;

public class ServicePlanRequest {

    private Long servicePackageId;
    private Long networkId;
    private String username;

    public Long getServicePackageId() {
        return servicePackageId;
    }

    public void setServicePackageId(Long servicePackageId) {
        this.servicePackageId = servicePackageId;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}