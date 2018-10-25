package shuchaowen.core.beans;

public interface BeanFactory {
	<T> T get(String name);

	<T> T get(Class<T> type);
	
	boolean contains(String name);
	
	Bean getBean(String name);
}
