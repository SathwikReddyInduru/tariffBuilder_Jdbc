//package com.xius.TariffBuilder.Entity;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.IdClass;
//import jakarta.persistence.Table;
//import lombok.Data;
//
//@Entity
//@Table(name = "CS_RAT_TARIFF_SERVICE_PACK_MAP")
//@IdClass(TariffServicePackMapId.class)
//@Data
//public class TariffServicePackMap {
//
//    @Id
//    @Column(name = "TARIFF_PACKAGE_ID")
//    private Long tariffPackageId;
//
//    @Id
//    @Column(name = "SERVICE_PACKAGE_ID")
//    private Long servicePackageId;
//
//    @Column(name = "NETWORK_ID")
//    private Long networkId;
//
//    @Column(name = "TARIFF_PLAN_TYPE")
//    private String tariffPlanType;
//
//    @Column(name = "CHARGE_ID")
//    private String chargeId;
//
//    @Column(name = "EFFECTIVE_START_OFFSET")
//    private Integer effectiveStartOffset;
//
//    @Column(name = "SERVICE_DURATION")
//    private Integer serviceDuration;
//
//    @Column(name = "PRIORITY")
//    private Integer priority;
//
//    @Column(name = "CREATED_OR_MODIFIED_DATE")
//    private java.util.Date createdOrModifiedDate;
//}