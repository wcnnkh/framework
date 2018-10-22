package shuchaowen.core.beans;

import java.util.List;

public interface PropertiesFactory {
	<T> T getProperties(String name, Class<T> type) throws Exception;
	
	List<BeanParameter> getBeanParameterList(String name);
}
