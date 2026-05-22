package com.xius.TariffBuilder.Dto;

import lombok.Data;

@Data
public class TariffPackageDetailsDto {

    private Long tariff_package_id;
    private String tariffPackageDesc;
    private Double activationFee;
    private String rentalType;
    private String balanceCategory;
    private String bucketUnitValue;
}