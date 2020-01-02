package scw.security.authority;

import java.util.Map;

public interface Authority {
	String getId();

	String getParentId();

	String getName();

	Map<String, String> getAttributeMap();
}
