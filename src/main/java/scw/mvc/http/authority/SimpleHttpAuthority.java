package scw.mvc.http.authority;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SimpleHttpAuthority implements Authority, Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private long parentId;
	private String name;
	private Map<String, String> attributeMap;
	private String method;
	private String requestPath;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getAttributeMap() {
		return attributeMap == null ? null : new HashMap<String, String>(
				attributeMap);
	}

	public void setAttributeMap(Map<String, String> attributeMap) {
		this.attributeMap = attributeMap;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}
}
