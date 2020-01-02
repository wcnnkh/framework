package scw.integration.app.pojo.model;

import java.io.Serializable;

import scw.core.utils.StringUtils;

/**
 * 用户地址模版
 * 
 * @author shuchaowen
 *
 */
public class UserAddressModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String province;// 省
	private String city;// 市
	private String district;// 区
	private String detailedAddress;// 详细地址
	private String phone;
	private String name;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getDetailedAddress() {
		return detailedAddress;
	}

	public void setDetailedAddress(String detailedAddress) {
		this.detailedAddress = detailedAddress;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompleteAddress() {
		StringBuilder sb = new StringBuilder();
		String province = getProvince();
		if (!StringUtils.isEmpty(province)) {
			sb.append(province);
		}

		String city = getCity();
		if (!StringUtils.isEmpty(city)) {
			sb.append(city);
		}

		String district = getDistrict();
		if (!StringUtils.isEmpty(district)) {
			sb.append(district);
		}

		String detailedAddress = getDetailedAddress();
		if (!StringUtils.isEmpty(detailedAddress)) {
			sb.append(detailedAddress);
		}
		return sb.toString();
	}

	public void setUserAddressModel(UserAddressModel userAddressModel) {
		setProvince(userAddressModel.getProvince());
		setCity(userAddressModel.getCity());
		setDistrict(userAddressModel.getDistrict());
		setDetailedAddress(userAddressModel.getDetailedAddress());
		setPhone(userAddressModel.getPhone());
		setName(userAddressModel.getName());
	}
}
