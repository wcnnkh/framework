package shuchaowen.core.beans;

public interface BeanInfoConfiguration {
	Bean getBean(String name);
	
	boolean contains(String name);
}
