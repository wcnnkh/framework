package scw.beans;

public interface BeanDefinition{
	String getId();

	String[] getNames();

	Class<?> getType();

	boolean isSingleton();

	boolean isProxy();
	
	void autowrite(Object bean) throws Exception;

	void init(Object bean) throws Exception;

	void destroy(Object bean) throws Exception;
	
	<T> T create();

	<T> T create(Object... params);
}
