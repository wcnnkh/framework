package scw.orm;

public interface MappingOperations {
	FieldDefinitionFactory getFieldDefinitionFactory();

	void setter(MappingContext context, Object bean, Object value) throws Exception;

	Object getter(MappingContext context, Object bean) throws Exception;

	Object getter(MappingContext context, Getter getter) throws Exception;

	<T> T create(MappingContext superContext, Class<T> clazz, Setter valueMapping) throws Exception;

	void iterator(MappingContext superContext, Class<?> clazz, IteratorMapping iterator) throws Exception;
}