package shuchaowen.core.beans;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationBeanFactory implements BeanFactory{
	private Map<String, String> nameMappingMap = new HashMap<String, String>();
	
	public Object get(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T get(Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean contains(String name) {
		return nameMappingMap.containsKey(name);
	}
}
