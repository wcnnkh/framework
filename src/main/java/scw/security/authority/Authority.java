package scw.security.authority;

import java.util.Map;

public interface Authority {
	long getId();

	long getParentId();

	String getName();

	Map<String, String> getAttributeMap();
}
