//package com.xius.TariffBuilder.Entity;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import lombok.Data;
//
//
//@Entity
//@Data
//@Table(name = "UMS_MT_USER")
//public class User {
//
//    @Id
//    @Column(name = "LOGIN_ID")
//    private String loginId;
//
//    @Column(name = "PASSWORD_NAME")
//    private String password;
//
//    @ManyToOne
//    @JoinColumn(name = "NETWORK_ID")
//    private Network network;
//
//}

package com.xius.TariffBuilder.Entity;

public class User {

    private String loginId;
    private String password;
    private Long networkId;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

	public Object getNetwork() {
		// TODO Auto-generated method stub
		return null;
	}
}