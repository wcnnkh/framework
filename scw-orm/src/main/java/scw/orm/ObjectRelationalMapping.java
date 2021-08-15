package scw.orm;

import java.util.Collection;

import scw.aop.support.ProxyUtils;
import scw.core.annotation.AnnotatedElementUtils;
import scw.mapper.Field;
import scw.mapper.FieldDescriptor;
import scw.mapper.FieldFactory;
import scw.mapper.Fields;
import scw.mapper.MapperUtils;
import scw.util.Accept;

/**
 * 对象映射关系
 * 
 * @author shuchaowen
 *
 */
public interface ObjectRelationalMapping extends FieldFactory{
	boolean ignore(FieldDescriptor fieldDescriptor);

	String getName(FieldDescriptor fieldDescriptor);

	Collection<String> getAliasNames(FieldDescriptor fieldDescriptor);

	String getName(Class<?> clazz);

	Collection<String> getAliasNames(Class<?> entityClass);

	/**
	 * 是否是主键
	 * 
	 * @param field
	 * @return
	 */
	boolean isPrimaryKey(FieldDescriptor fieldDescriptor);

	default boolean isPrimaryKey(Field field) {
		return (field.isSupportGetter() && isPrimaryKey(field.getGetter()))
				|| (field.isSupportSetter() && isPrimaryKey(field.getSetter()));
	}

	default Accept<Field> getPrimaryKeyAccept() {
		return new Accept<Field>() {

			@Override
			public boolean accept(Field field) {
				return isPrimaryKey(field);
			}
		};
	}

	default boolean isNullable(FieldDescriptor fieldDescriptor) {
		return AnnotatedElementUtils.isNullable(fieldDescriptor, () -> !isPrimaryKey(fieldDescriptor));
	}

	default boolean isNullable(Field field) {
		return (field.isSupportGetter() && isNullable(field.getGetter()))
				|| (field.isSupportSetter() && isNullable(field.getSetter()));
	}

	/**
	 * 是否是一个实体类
	 * 
	 * @param field
	 * @return
	 */
	boolean isEntity(FieldDescriptor fieldDescriptor);
	
	boolean isEntity(Class<?> clazz);

	boolean isVersionField(FieldDescriptor fieldDescriptor);

	default Accept<Field> getEntityAccept() {
		return new Accept<Field>() {

			@Override
			public boolean accept(Field field) {
				return (field.isSupportGetter() && isEntity(field.getGetter()))
						|| (field.isSupportSetter() && isEntity(field.getSetter()));
			}
		};
	}

	default Fields getFields(Class<?> clazz, Field parentField) {
		return MapperUtils.getFields(ProxyUtils.getFactory().getUserClass(clazz), parentField).entity();
	}

	default Fields getPrimaryKeys(Class<?> clazz) {
		return getFields(clazz).accept(getPrimaryKeyAccept());
	}

	default Fields getNotPrimaryKeys(Class<?> clazz) {
		return getFields(clazz).accept(getEntityAccept().negate()).accept(getPrimaryKeyAccept().negate());
	}
	
	EntityStructure<? extends Property> resolve(Class<?> entityClass);
}
