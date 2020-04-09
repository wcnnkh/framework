package scw.kdniao;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.util.ToMap;

/**
 * 收件人或发件人
 * 
 * @author shuchaowen
 *
 */
public class ReceiverOrSender implements Serializable, ToMap<String, Object> {
	private static final long serialVersionUID = 1L;
	// 公司 选填
	private String company;
	// 名称
	private String name;
	// 电话(电话与手机，必填一个)
	private String tel;
	// 手机
	private String mobile;
	// 邮编 选填
	private String postCode;
	// 省（如广东省，不要缺少“省”）
	private String provinceName;
	// 市（如深圳市，不要缺少“市”）
	private String cityName;
	// 区（如福田区，不要缺少“区”或“县”）
	private String expAreaName;
	// 详细地址
	private String address;

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getExpAreaName() {
		return expAreaName;
	}

	public void setExpAreaName(String expAreaName) {
		this.expAreaName = expAreaName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("Company", company);
		map.put("Name", name);
		map.put("Tel", tel);
		map.put("Mobile", mobile);
		map.put("PostCode", postCode);
		map.put("ProvinceName", provinceName);
		map.put("CityName", cityName);
		map.put("ExpAreaName", expAreaName);
		map.put("Address", address);
		return map;
	}
}
