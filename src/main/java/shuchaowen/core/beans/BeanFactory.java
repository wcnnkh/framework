package shuchaowen.core.beans;

public interface BeanFactory {
	<T> T get(final String name);

	<T> T get(final Class<T> type);
	
	boolean contains(final String name);
	
	BeanInfo getBeanInfo(String name);
}
