package scw.servlet.beans;

public interface RequestBeanFactory {
	RequestBean get(String name);
	
	boolean contains(String name);
}
