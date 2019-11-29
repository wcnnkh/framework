package scw.orm;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

import scw.core.annotation.Ignore;
import scw.core.reflect.AnnotationUtils;
import scw.core.utils.IteratorCallback;
import scw.orm.annotation.NotColumn;

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

	Collection<MappingContext> getMappingContexts(Class<?> clazz, IteratorCallback<MappingContext> filter);

	Collection<MappingContext> getMappingContexts(MappingContext superContext, Class<?> clazz,
			IteratorCallback<MappingContext> filter);

	boolean isPrimaryKey(MappingContext mappingContext);

	Collection<MappingContext> getPrimaryKeys(MappingContext supperContext, Class<?> clazz);

	Collection<MappingContext> getPrimaryKeys(Class<?> clazz);

	<T> String getObjectKey(Class<? extends T> clazz, T bean);

	String getObjectKeyById(Class<?> clazz, Collection<Object> primaryKeys);

	<K> Map<String, K> getInIdKeyMap(Class<?> clazz, Collection<K> lastPrimaryKeys, Object[] primaryKeys);
	
	boolean isIgnore(MappingContext context);
}