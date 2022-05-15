package io.basc.framework.orm.support;

import java.util.Collection;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.data.domain.Range;
import io.basc.framework.orm.ObjectRelationalResolver;
import io.basc.framework.util.comparator.Sort;

public class AbstractObjectRelationalExtend implements
		ObjectRelationalResolverExtend {

	@Override
	public Boolean isIgnore(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.isIgnore(entityClass, descriptor);
	}

	@Override
	public String getName(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain) {
		return chain.getName(entityClass, descriptor);
	}

	@Override
	public Collection<String> getAliasNames(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.getAliasNames(entityClass, descriptor);
	}

	@Override
	public String getName(Class<?> entityClass, ObjectRelationalResolver chain) {
		return chain.getName(entityClass);
	}

	@Override
	public Collection<String> getAliasNames(Class<?> entityClass,
			ObjectRelationalResolver chain) {
		return chain.getAliasNames(entityClass);
	}

	@Override
	public Boolean isPrimaryKey(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.isPrimaryKey(entityClass, descriptor);
	}

	@Override
	public Boolean isNullable(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.isNullable(entityClass, descriptor);
	}

	@Override
	public Boolean isEntity(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.isEntity(entityClass, descriptor);
	}

	@Override
	public Boolean isEntity(Class<?> entityClass, ObjectRelationalResolver chain) {
		return chain.isEntity(entityClass);
	}

	@Override
	public Boolean isVersionField(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.isVersionField(entityClass, descriptor);
	}

	@Override
	public Collection<Range<Double>> getNumberRanges(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.getNumberRanges(entityClass, descriptor);
	}

	@Override
	public Boolean isAutoIncrement(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.isAutoIncrement(entityClass, descriptor);
	}

	@Override
	public Boolean isIgnore(Class<?> entityClass, ObjectRelationalResolver chain) {
		return chain.isIgnore(entityClass);
	}

	@Override
	public String getComment(Class<?> entityClass,
			ObjectRelationalResolver chain) {
		return chain.getComment(entityClass);
	}

	@Override
	public String getComment(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.getComment(entityClass, descriptor);
	}

	@Override
	public String getCharsetName(Class<?> entityClass,
			ObjectRelationalResolver chain) {
		return chain.getCharsetName(entityClass);
	}

	@Override
	public String getCharsetName(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.getCharsetName(entityClass, descriptor);
	}

	@Override
	public Boolean isUnique(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.isUnique(entityClass, descriptor);
	}

	@Override
	public Boolean isIncrement(Class<?> entityClass,
			ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.isIncrement(entityClass, descriptor);
	}

	@Override
	public Sort getSort(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain) {
		return chain.getSort(entityClass, descriptor);
	}

}
