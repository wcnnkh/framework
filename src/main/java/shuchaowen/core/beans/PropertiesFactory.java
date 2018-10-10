package shuchaowen.core.beans;

import java.util.Map;

public interface PropertiesFactory {
	Map<String, String> getPropertieMap(String filePath);
	
	String getValue(String key);
}
