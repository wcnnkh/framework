package scw.security.authority;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class DefaultAuthority implements Authority, Serializable {
	private static final long serialVersionUID = 1L;
	private final String id;
	private final String parentId;
	private final String name;
	private final Map<String, String> attributeMap;

	public DefaultAuthority(String id, String parentId, String name,
			Map<String, String> attributeMap) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.attributeMap = attributeMap;
	}

	public String getId() {
		return id;
	}

	public String getParentId() {
		return parentId;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getAttributeMap() {
		return attributeMap == null ? Collections.EMPTY_MAP : Collections
				.unmodifiableMap(attributeMap);
	}
}
