package scw.mvc.beans;

public interface HttpChannelBeanManager {
	<T> T getBean(String name);

	<T> T getBean(Class<T> type);
}
