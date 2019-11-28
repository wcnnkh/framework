package scw.orm;

import java.util.Collection;

import scw.core.utils.IteratorCallback;

public interface Mapper {
	FieldDefinitionFactory getFieldDefinitionFactory();

	void setter(MappingContext context, Object bean, Object value) throws Exception;

	void setter(MappingContext context, Setter setter, Object value) throws Exception;

	Object getter(MappingContext context, Object bean) throws Exception;

	Object getter(MappingContext context, Getter getter) throws Exception;

	<T> T newInstance(Class<T> type);

	<T> T create(MappingContext superContext, Class<T> clazz, SetterMapping<? extends Mapper> setterMapping)
			throws Exception;

	void iterator(MappingContext superContext, Class<?> clazz, IteratorMapping<? extends Mapper> iterator)
			throws Exception;

	Collection<MappingContext> getMappingContexts(Class<?> clazz, IteratorCallback<MappingContext> filter);

	Collection<MappingContext> getMappingContexts(MappingContext superContext, Class<?> clazz,
			IteratorCallback<MappingContext> filter);
}