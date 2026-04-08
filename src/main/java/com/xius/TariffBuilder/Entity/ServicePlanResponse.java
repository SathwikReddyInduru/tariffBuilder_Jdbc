package com.xius.TariffBuilder.Entity;

import java.util.Date;

public class ServicePlanResponse {

    private Long networkId;
    private Long servicePlanId;
    private String servicePlanDesc;
    private String servicePlanType;
    private Integer typeOfService;
    private Integer priority;
    private String limitedHoursYn;
    private Integer servicePlanFreqFromHrs;
    private Integer servicePlanFreqToHrs;
    private String allowMtc;
    private String allowMoc;
    private String allowNldMo;
    private String allowIldMo;
    private String allowData;
    private String ratingType;
    private Long zoneGroupId;
    private Long smsZoneGroupId;
    private Long nsLocalOnnetCalendarId;
    private Long nsLocalOffnetCalendarId;
    private Long nsNldCalendarId;
    private Long nsIldCalendarId;
    private String limitedNetworksYn;
    private String bndlWithSpYn;
    private String bundleId;
    private Date createdDate;
    private String createdBy;
    private Long smsCalendarId;
    private Long fnfMaxLines;
    private String status;
    private Long fnfMaxGroups;
    private String groupsAllowed;
	public Long getNetworkId() {
		return networkId;
	}
	public void setNetworkId(Long networkId) {
		this.networkId = networkId;
	}
	public Long getServicePlanId() {
		return servicePlanId;
	}
	public void setServicePlanId(Long servicePlanId) {
		this.servicePlanId = servicePlanId;
	}
	public String getServicePlanDesc() {
		return servicePlanDesc;
	}
	public void setServicePlanDesc(String servicePlanDesc) {
		this.servicePlanDesc = servicePlanDesc;
	}
	public String getServicePlanType() {
		return servicePlanType;
	}
	public void setServicePlanType(String servicePlanType) {
		this.servicePlanType = servicePlanType;
	}
	public Integer getTypeOfService() {
		return typeOfService;
	}
	public void setTypeOfService(Integer typeOfService) {
		this.typeOfService = typeOfService;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public String getLimitedHoursYn() {
		return limitedHoursYn;
	}
	public void setLimitedHoursYn(String limitedHoursYn) {
		this.limitedHoursYn = limitedHoursYn;
	}
	public Integer getServicePlanFreqFromHrs() {
		return servicePlanFreqFromHrs;
	}
	public void setServicePlanFreqFromHrs(Integer servicePlanFreqFromHrs) {
		this.servicePlanFreqFromHrs = servicePlanFreqFromHrs;
	}
	public Integer getServicePlanFreqToHrs() {
		return servicePlanFreqToHrs;
	}
	public void setServicePlanFreqToHrs(Integer servicePlanFreqToHrs) {
		this.servicePlanFreqToHrs = servicePlanFreqToHrs;
	}
	public String getAllowMtc() {
		return allowMtc;
	}
	public void setAllowMtc(String allowMtc) {
		this.allowMtc = allowMtc;
	}
	public String getAllowMoc() {
		return allowMoc;
	}
	public void setAllowMoc(String allowMoc) {
		this.allowMoc = allowMoc;
	}
	public String getAllowNldMo() {
		return allowNldMo;
	}
	public void setAllowNldMo(String allowNldMo) {
		this.allowNldMo = allowNldMo;
	}
	public String getAllowIldMo() {
		return allowIldMo;
	}
	public void setAllowIldMo(String allowIldMo) {
		this.allowIldMo = allowIldMo;
	}
	public String getAllowData() {
		return allowData;
	}
	public void setAllowData(String allowData) {
		this.allowData = allowData;
	}
	public String getRatingType() {
		return ratingType;
	}
	public void setRatingType(String ratingType) {
		this.ratingType = ratingType;
	}
	public Long getZoneGroupId() {
		return zoneGroupId;
	}
	public void setZoneGroupId(Long zoneGroupId) {
		this.zoneGroupId = zoneGroupId;
	}
	public Long getSmsZoneGroupId() {
		return smsZoneGroupId;
	}
	public void setSmsZoneGroupId(Long smsZoneGroupId) {
		this.smsZoneGroupId = smsZoneGroupId;
	}
	public Long getNsLocalOnnetCalendarId() {
		return nsLocalOnnetCalendarId;
	}
	public void setNsLocalOnnetCalendarId(Long nsLocalOnnetCalendarId) {
		this.nsLocalOnnetCalendarId = nsLocalOnnetCalendarId;
	}
	public Long getNsLocalOffnetCalendarId() {
		return nsLocalOffnetCalendarId;
	}
	public void setNsLocalOffnetCalendarId(Long nsLocalOffnetCalendarId) {
		this.nsLocalOffnetCalendarId = nsLocalOffnetCalendarId;
	}
	public Long getNsNldCalendarId() {
		return nsNldCalendarId;
	}
	public void setNsNldCalendarId(Long nsNldCalendarId) {
		this.nsNldCalendarId = nsNldCalendarId;
	}
	public Long getNsIldCalendarId() {
		return nsIldCalendarId;
	}
	public void setNsIldCalendarId(Long nsIldCalendarId) {
		this.nsIldCalendarId = nsIldCalendarId;
	}
	public String getLimitedNetworksYn() {
		return limitedNetworksYn;
	}
	public void setLimitedNetworksYn(String limitedNetworksYn) {
		this.limitedNetworksYn = limitedNetworksYn;
	}
	public String getBndlWithSpYn() {
		return bndlWithSpYn;
	}
	public void setBndlWithSpYn(String bndlWithSpYn) {
		this.bndlWithSpYn = bndlWithSpYn;
	}
	public String getBundleId() {
		return bundleId;
	}
	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Long getSmsCalendarId() {
		return smsCalendarId;
	}
	public void setSmsCalendarId(Long smsCalendarId) {
		this.smsCalendarId = smsCalendarId;
	}
	public Long getFnfMaxLines() {
		return fnfMaxLines;
	}
	public void setFnfMaxLines(Long fnfMaxLines) {
		this.fnfMaxLines = fnfMaxLines;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getFnfMaxGroups() {
		return fnfMaxGroups;
	}
	public void setFnfMaxGroups(Long fnfMaxGroups) {
		this.fnfMaxGroups = fnfMaxGroups;
	}
	public String getGroupsAllowed() {
		return groupsAllowed;
	}
	public void setGroupsAllowed(String groupsAllowed) {
		this.groupsAllowed = groupsAllowed;
	}
	@Override
	public String toString() {
		return "ServicePlanResponse [networkId=" + networkId + ", servicePlanId=" + servicePlanId + ", servicePlanDesc="
				+ servicePlanDesc + ", servicePlanType=" + servicePlanType + ", typeOfService=" + typeOfService
				+ ", priority=" + priority + ", limitedHoursYn=" + limitedHoursYn + ", servicePlanFreqFromHrs="
				+ servicePlanFreqFromHrs + ", servicePlanFreqToHrs=" + servicePlanFreqToHrs + ", allowMtc=" + allowMtc
				+ ", allowMoc=" + allowMoc + ", allowNldMo=" + allowNldMo + ", allowIldMo=" + allowIldMo
				+ ", allowData=" + allowData + ", ratingType=" + ratingType + ", zoneGroupId=" + zoneGroupId
				+ ", smsZoneGroupId=" + smsZoneGroupId + ", nsLocalOnnetCalendarId=" + nsLocalOnnetCalendarId
				+ ", nsLocalOffnetCalendarId=" + nsLocalOffnetCalendarId + ", nsNldCalendarId=" + nsNldCalendarId
				+ ", nsIldCalendarId=" + nsIldCalendarId + ", limitedNetworksYn=" + limitedNetworksYn
				+ ", bndlWithSpYn=" + bndlWithSpYn + ", bundleId=" + bundleId + ", createdDate=" + createdDate
				+ ", createdBy=" + createdBy + ", smsCalendarId=" + smsCalendarId + ", fnfMaxLines=" + fnfMaxLines
				+ ", status=" + status + ", fnfMaxGroups=" + fnfMaxGroups + ", groupsAllowed=" + groupsAllowed + "]";
	}
    

}