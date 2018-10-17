package shuchaowen.core.beans;

import java.util.List;

public interface PropertiesFactory {
	<T> T getProperties(String name, Class<T> type) throws Exception;
	
	List<BeanProperties> getBeanPropertiesList(String name);
	
	List<BeanMethodParameter> getBeanMethodParameterList(String name);
}
