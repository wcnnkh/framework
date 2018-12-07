package shuchaowen.beans;

public interface BeanFactory {
	<T> T get(String name);

	<T> T get(Class<T> type);
	
	Bean getBean(String name);
	
	boolean contains(String name);
}
