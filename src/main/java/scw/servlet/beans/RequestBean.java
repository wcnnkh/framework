package scw.servlet.beans;

import javax.servlet.ServletRequest;

public interface RequestBean {
	String getId();

	String[] getNames();
	
	Class<?> getType();

	<T> T newInstance(ServletRequest request);
	
	void autowrite(Object bean) throws Exception;

	void init(Object bean) throws Exception;

	void destroy(Object bean) throws Exception;
}
