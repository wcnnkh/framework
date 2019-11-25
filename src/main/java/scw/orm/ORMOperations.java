package scw.orm;

public interface ORMOperations {
	FieldDefinitionFactory getFieldDefinitionFactory();

	void setter(FieldDefinitionContext context, Object bean, Object value) throws Exception;

	Object getter(FieldDefinitionContext context, Object bean) throws Exception;

	<T> T create(FieldDefinitionContext superContext, Class<T> clazz, ValueFactory valueFactory) throws Exception;

	void iterator(FieldDefinitionContext superContext, Class<?> clazz, FieldDefintionIterator iterator) throws Exception;
}