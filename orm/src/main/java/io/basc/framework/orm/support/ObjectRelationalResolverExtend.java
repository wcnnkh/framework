package io.basc.framework.orm.support;

import java.util.Collection;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.data.domain.Range;
import io.basc.framework.lang.Nullable;
import io.basc.framework.orm.ObjectRelationalResolver;
import io.basc.framework.util.comparator.Sort;

public interface ObjectRelationalResolverExtend {
	Boolean isIgnore(Class<?> entityClass, ObjectRelationalResolver chain);

	Boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain);

	String getName(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain);

	Collection<String> getAliasNames(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain);

	String getName(Class<?> entityClass, ObjectRelationalResolver chain);

	Collection<String> getAliasNames(Class<?> entityClass,
			ObjectRelationalResolver chain);

	Boolean isPrimaryKey(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain);

	Boolean isNullable(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain);

	Boolean isEntity(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain);

	Boolean isEntity(Class<?> entityClass, ObjectRelationalResolver chain);

	Boolean isVersionField(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain);

	/**
	 * 获取值的范围
	 * 
	 * @param entityClass
	 * @param descriptor
	 * @return
	 */
	Collection<Range<Double>> getNumberRanges(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain);

	/**
	 * 是否自增
	 * 
	 * @param entityClass
	 * @param descriptor
	 * @param chain
	 * @return
	 */
	Boolean isAutoIncrement(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain);

	String getComment(Class<?> entityClass, ObjectRelationalResolver chain);

	String getComment(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain);

	String getCharsetName(Class<?> entityClass, ObjectRelationalResolver chain);

	String getCharsetName(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain);

	Boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain);

	Boolean isIncrement(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain);

	@Nullable
	Sort getSort(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain);
}
