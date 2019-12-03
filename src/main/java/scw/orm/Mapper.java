package scw.orm;

import java.util.Collection;
import java.util.Map;

import scw.core.utils.IteratorCallback;

public interface Mapper extends ColumnFactory {

	void setter(MappingContext context, Object bean, Object value) throws Exception;

	void setter(MappingContext context, Setter setter, Object value) throws Exception;

	Object getter(MappingContext context, Object bean) throws Exception;

	Object getter(MappingContext context, Getter getter) throws Exception;

	<T> T newInstance(Class<T> type);

	<T> T create(MappingContext superContext, Class<T> clazz, SetterMapping<? extends Mapper> setterMapping)
			throws Exception;

	void iterator(MappingContext superContext, Class<?> clazz, IteratorMapping<? extends Mapper> iterator)
			throws Exception;

	boolean isPrimaryKey(MappingContext mappingContext);
	
	boolean isIgnore(MappingContext context);

	boolean isEntity(MappingContext context);
	
	boolean isNullable(MappingContext context);

	Collection<MappingContext> getPrimaryKeys(MappingContext supperContext, Class<?> clazz);

	Collection<MappingContext> getPrimaryKeys(Class<?> clazz);

	<T> String getObjectKey(Class<? extends T> clazz, T bean);

	String getObjectKeyById(Class<?> clazz, Collection<Object> primaryKeys);

	<K> Map<String, K> getInIdKeyMap(Class<?> clazz, Collection<K> lastPrimaryKeys, Object[] primaryKeys);

	Collection<MappingContext> getNotPrimaryKeys(MappingContext supperContext, Class<?> clazz);

	Collection<MappingContext> getNotPrimaryKeys(Class<?> clazz);

	Collection<MappingContext> getMappingContexts(Class<?> clazz, IteratorCallback<MappingContext> filter);

	Collection<MappingContext> getMappingContexts(MappingContext superContext, Class<?> clazz,
			IteratorCallback<MappingContext> filter);

	ObjectRelationalMapping getObjectRelationalMapping(MappingContext superContext, Class<?> clazz);

	ObjectRelationalMapping getObjectRelationalMapping(Class<?> clazz);
}