//package com.xius.TariffBuilder.Entity;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import lombok.Data;
//
//@Entity
//@Data
//@Table(name = "cs_tariff_pack_ap_reg_status")
//public class TariffEntity {
////
//    @Id
//    @Column(name = "TARIFF_PACKAGE_ID")
//    private Long tariffPackageId;
//
//    @Column(name = "NETWORK_ID")
//    private Long networkId;
//
//    @Column(name = "TARIFF_PACKAGE_NAME")
//    private String tariffPackageName;
//
//    @Column(name = "STATUS")
//    private String status;
//}

package com.xius.TariffBuilder.Entity;

public class TariffEntity {

    private Long networkId;
    private Long tariffPackageId;
    private String tariffPackageName;
    private String status;


    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    public Long getTariffPackageId() {
        return tariffPackageId;
    }

    public void setTariffPackageId(Long tariffPackageId) {
        this.tariffPackageId = tariffPackageId;
    }

    public String getTariffPackageName() {
        return tariffPackageName;
    }

    public void setTariffPackageName(String tariffPackageName) {
        this.tariffPackageName = tariffPackageName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}