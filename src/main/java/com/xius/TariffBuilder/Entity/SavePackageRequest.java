package com.xius.TariffBuilder.Entity;

import java.util.List;

import lombok.Data;

public class SavePackageRequest {

    private Long networkId;
    private String username;

    private String packageType;
    private String tariffPackCategory;

    private Long tariffPlanId;

    private List<AtpItem> defaultAtps;
    private List<AtpItem> allowedAtps;

    private String tariffPackageDesc;
    private String endDate;
    private String publicityId;
    private String chargeId;

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

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public String getTariffPackCategory() {
		return tariffPackCategory;
	}

	public void setTariffPackCategory(String tariffPackCategory) {
		this.tariffPackCategory = tariffPackCategory;
	}

	public Long getTariffPlanId() {
		return tariffPlanId;
	}

	public void setTariffPlanId(Long tariffPlanId) {
		this.tariffPlanId = tariffPlanId;
	}

	public List<AtpItem> getDefaultAtps() {
		return defaultAtps;
	}

	public void setDefaultAtps(List<AtpItem> defaultAtps) {
		this.defaultAtps = defaultAtps;
	}

	public List<AtpItem> getAllowedAtps() {
		return allowedAtps;
	}

	public void setAllowedAtps(List<AtpItem> allowedAtps) {
		this.allowedAtps = allowedAtps;
	}

	public String getTariffPackageDesc() {
		return tariffPackageDesc;
	}

	public void setTariffPackageDesc(String tariffPackageDesc) {
		this.tariffPackageDesc = tariffPackageDesc;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getPublicityId() {
		return publicityId;
	}

	public void setPublicityId(String publicityId) {
		this.publicityId = publicityId;
	}



	public String getChargeId() {
		return chargeId;
	}

	public void setChargeId(String chargeId) {
		this.chargeId = chargeId;
	}

	public Long getSubscriberCategoryId() {
		return subscriberCategoryId;
	}

	public void setSubscriberCategoryId(Long subscriberCategoryId) {
		this.subscriberCategoryId = subscriberCategoryId;
	}

	public String getIsCorporateYn() {
		return isCorporateYn;
	}

	public void setIsCorporateYn(String isCorporateYn) {
		this.isCorporateYn = isCorporateYn;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private Long subscriberCategoryId;
    private String isCorporateYn;
    private String description;

    @Data
    public static class AtpItem {

        private Long servicePackageId;
        private String chargeId;

        private Integer priority;
        private Integer effectiveStartOffset;
        private Integer serviceDuration;
    }
}