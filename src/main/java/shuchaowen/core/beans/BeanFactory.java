package shuchaowen.core.beans;

public interface BeanFactory {
	Object get(final String name);

	<T> T get(final Class<T> type);
	
	boolean contains(final String name);
}
