package scw.orm;

public interface SetterHandler {
	void handler(Class<?> clazz, Object bean, ValueFactory valueFactory);
}
