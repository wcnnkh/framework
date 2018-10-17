package shuchaowen.core.beans;

public interface PropertiesFactory {
	<T> T getProperties(String name, Class<T> type) throws Exception;
}
