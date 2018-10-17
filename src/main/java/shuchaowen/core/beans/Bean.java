package shuchaowen.core.beans;

public interface Bean {
	String getId();
	
	String[] getNames();
	
	Class<?> getType();
	
	boolean isSingleton();
	
	boolean isProxy();
	
	<T> T newInstance();
	
	<T> T newInstance(Class<?>[] parameterTypes, Object ...args);
	
	void wrapper(Object bean) throws Exception;
	
	void destroy(Object bean) throws Exception;
}
