package scw.orm;

public interface ORMInstanceFactory {
	<T> T newInstance(Class<? extends T> clazz);
}
