package scw.utils.express.kdniao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import scw.core.net.http.ToParameterMap;

public class AddService implements Serializable, ToParameterMap {
	private static final long serialVersionUID = 1L;
	private String name;
	private String value;
	private String customerId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Map<String, Object> toRequestParameterMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("Name", name);
		map.put("Value", value);
		map.put("CustomerID", customerId);
		return map;
	}
}
