package scw.security.authority;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleAuthority implements Authority, Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String parentId;
	private String name;
	private Map<String, String> attributeMap;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getAttributeMap() {
		return attributeMap == null ? null : new LinkedHashMap<String, String>(attributeMap);
	}

	public void setAttributeMap(Map<String, String> attributeMap) {
		this.attributeMap = attributeMap;
	}
}
