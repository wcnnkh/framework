package scw.core.instance;

public interface InstanceFactory {

	<T> T get(Class<T> type);

}
