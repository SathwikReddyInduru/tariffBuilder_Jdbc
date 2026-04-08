package com.xius.TariffBuilder.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "CS_RAT_PERIODIC_CHARGE_INFO")
@Data
public class PeriodicChargeInfo {

    @Id
    @Column(name = "CHARGE_ID")
    private String chargeId;

    @Column(name = "NETWORK_ID")
    private Long networkId;

    @Column(name = "SERVICE_TYPE")
    private Integer serviceType;

    @Column(name = "RENTAL_TYPE")
    private String rentalType;

    @Column(name = "RENTAL_PERIOD")
    private Integer rentalPeriod;

    @Column(name = "CREATED_BY")
    private String createdBy;
}
