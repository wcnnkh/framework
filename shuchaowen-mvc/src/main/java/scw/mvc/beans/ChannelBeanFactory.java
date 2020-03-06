package scw.mvc.beans;

public interface ChannelBeanFactory {
	<T> T getBean(String name);

	<T> T getBean(Class<T> type);
}
