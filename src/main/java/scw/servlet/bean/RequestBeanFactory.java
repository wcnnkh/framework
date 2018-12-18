package scw.servlet.bean;

public interface RequestBeanFactory {
	RequestBean get(String name);
	
	boolean contains(String name);
}
