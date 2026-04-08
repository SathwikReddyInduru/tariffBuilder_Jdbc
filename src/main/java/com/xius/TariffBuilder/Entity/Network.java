package com.xius.TariffBuilder.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "GLB_MT_NETWORK")
public class Network {

    @Id
    @Column(name = "NETWORK_ID")
    private Long networkId;

    @Column(name = "NETWORK_DISPLAY")
    private String networkDisplay;

}