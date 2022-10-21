package io.basc.framework.security.authority;

import io.basc.framework.json.JsonUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class DefaultAuthority implements Authority, Serializable {
	private static final long serialVersionUID = 1L;
	private final String id;
	private final String parentId;
	private final String name;
	private final Map<String, String> attributeMap;
	private final boolean menu;

	public DefaultAuthority(String id, String parentId, String name,
			Map<String, String> attributeMap, boolean menu) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.attributeMap = attributeMap;
		this.menu = menu;
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

	public boolean isMenu() {
		return menu;
	}
	
	@Override
	public String toString() {
		return JsonUtils.toJsonString(this);
	}
}
