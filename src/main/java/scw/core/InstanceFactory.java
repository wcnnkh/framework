package scw.core;

public interface InstanceFactory {
	<T> T getInstance(Class<T> type);
}
