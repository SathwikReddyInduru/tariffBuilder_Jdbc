package com.xius.TariffBuilder.Entity;

public class TariffHierarchy {

    private Long tariffPackageId;
    private Long networkId;
    private String publicityId;
    private Long servicePackageId;
    private String tariffPlanType;
    private Long priority;

    public Long getTariffPackageId() {
        return tariffPackageId;
    }

    public void setTariffPackageId(Long tariffPackageId) {
        this.tariffPackageId = tariffPackageId;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    public String getPublicityId() {
        return publicityId;
    }

    public void setPublicityId(String publicityId) {
        this.publicityId = publicityId;
    }

    public Long getServicePackageId() {
        return servicePackageId;
    }

    public void setServicePackageId(Long servicePackageId) {
        this.servicePackageId = servicePackageId;
    }

    public String getTariffPlanType() {
        return tariffPlanType;
    }

    public void setTariffPlanType(String tariffPlanType) {
        this.tariffPlanType = tariffPlanType;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }
}