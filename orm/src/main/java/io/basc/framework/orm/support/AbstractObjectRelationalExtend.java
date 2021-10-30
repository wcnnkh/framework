package io.basc.framework.orm.support;

import java.util.Collection;

import io.basc.framework.data.domain.Range;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.orm.ObjectRelationalResolver;

public class AbstractObjectRelationalExtend implements ObjectRelationalResolverExtend {

	@Override
	public Boolean isIgnore(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		return chain.isIgnore(entityClass, fieldDescriptor);
	}

	@Override
	public String getName(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		return chain.getName(entityClass, fieldDescriptor);
	}

	@Override
	public Collection<String> getAliasNames(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain) {
		return chain.getAliasNames(entityClass, fieldDescriptor);
	}

	@Override
	public String getName(Class<?> entityClass, ObjectRelationalResolver chain) {
		return chain.getName(entityClass);
	}

	@Override
	public Collection<String> getAliasNames(Class<?> entityClass, ObjectRelationalResolver chain) {
		return chain.getAliasNames(entityClass);
	}

	@Override
	public Boolean isPrimaryKey(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		return chain.isPrimaryKey(entityClass, fieldDescriptor);
	}

	@Override
	public Boolean isNullable(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		return chain.isNullable(entityClass, fieldDescriptor);
	}

	@Override
	public Boolean isEntity(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		return chain.isEntity(entityClass, fieldDescriptor);
	}

	@Override
	public Boolean isEntity(Class<?> entityClass, ObjectRelationalResolver chain) {
		return chain.isEntity(entityClass);
	}

	@Override
	public Boolean isVersionField(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain) {
		return chain.isVersionField(entityClass, fieldDescriptor);
	}

	@Override
	public Collection<Range<Double>> getNumberRanges(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain) {
		return chain.getNumberRanges(entityClass, fieldDescriptor);
	}

	@Override
	public Boolean isAutoIncrement(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain) {
		return chain.isAutoIncrement(entityClass, fieldDescriptor);
	}

	@Override
	public Boolean isIgnore(Class<?> entityClass, ObjectRelationalResolver chain) {
		return chain.isIgnore(entityClass);
	}

	@Override
	public String getComment(Class<?> entityClass, ObjectRelationalResolver chain) {
		return chain.getComment(entityClass);
	}

	@Override
	public String getComment(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		return chain.getComment(entityClass, fieldDescriptor);
	}

	@Override
	public String getCharsetName(Class<?> entityClass, ObjectRelationalResolver chain) {
		return chain.getCharsetName(entityClass);
	}

	@Override
	public String getCharsetName(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain) {
		return chain.getCharsetName(entityClass, fieldDescriptor);
	}

	@Override
	public Boolean isUnique(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		return chain.isUnique(entityClass, fieldDescriptor);
	}

	@Override
	public Boolean isIncrement(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain) {
		return chain.isIncrement(entityClass, fieldDescriptor);
	}

}
