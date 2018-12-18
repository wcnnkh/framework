package scw.web.servlet.context;

public interface RequestBeanContext {
	<T> T getBean(Class<T> type);
	
	<T> T getBean(Class<T> type, String name);
	
	void destroy();
}
