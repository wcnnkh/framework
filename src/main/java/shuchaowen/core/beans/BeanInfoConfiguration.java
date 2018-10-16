package shuchaowen.core.beans;

public interface BeanInfoConfiguration {
	Bean getBean(Class<?> type);
	
	Bean getBean(String name);
	
	boolean contains(String name);
}
