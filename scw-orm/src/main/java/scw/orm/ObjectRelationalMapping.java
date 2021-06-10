package scw.orm;

import java.util.Collection;

import scw.aop.support.ProxyUtils;
import scw.lang.Nullable;
import scw.mapper.Field;
import scw.mapper.FieldDescriptor;
import scw.mapper.FieldFeature;
import scw.mapper.Fields;
import scw.mapper.MapperUtils;
import scw.util.Accept;

/**
 * 对象映射关系
 * 
 * @author shuchaowen
 *
 */
public interface ObjectRelationalMapping {
	static final Accept<Field> GETTER_ACCEPT = FieldFeature.SUPPORT_GETTER.and(FieldFeature.IGNORE_STATIC);
	static final Accept<Field> SETTER_ACCEPT = FieldFeature.SUPPORT_SETTER.and(FieldFeature.IGNORE_STATIC);

	boolean ignore(FieldDescriptor fieldDescriptor);

	String getName(FieldDescriptor fieldDescriptor);

	Collection<String> getAliasNames(FieldDescriptor fieldDescriptor);

	String getName(Class<?> clazz);

	Collection<String> getAliasNames(Class<?> entityClass);

	/**
	 * 字段描述
	 * 
	 * @param field
	 * @return
	 */
	@Nullable
	String getDescription(FieldDescriptor fieldDescriptor);

	/**
	 * 是否是主键
	 * 
	 * @param field
	 * @return
	 */
	boolean isPrimaryKey(FieldDescriptor fieldDescriptor);

	/**
	 * 是否是一个实体类
	 * 
	 * @param field
	 * @return
	 */
	boolean isEntity(FieldDescriptor fieldDescriptor);

	boolean isEntity(Class<?> clazz);
	
	boolean isVersionField(FieldDescriptor fieldDescriptor);

	default Accept<FieldDescriptor> getPrimaryKeyAccept() {
		return new Accept<FieldDescriptor>() {
			@Override
			public boolean accept(FieldDescriptor e) {
				return isPrimaryKey(e) && !ignore(e);
			}
		};
	}

	default Accept<FieldDescriptor> getEntityAccept() {
		return new Accept<FieldDescriptor>() {

			@Override
			public boolean accept(FieldDescriptor e) {
				return isEntity(e) && !ignore(e);
			}
		};
	}

	default Fields getGetterFields(Class<?> clazz, boolean useSuperClass, Field parentField) {
		return MapperUtils.getMapper()
				.getFields(ProxyUtils.getFactory().getUserClass(clazz), useSuperClass, parentField)
				.accept(GETTER_ACCEPT).accept(new Accept<Field>() {

					@Override
					public boolean accept(Field e) {
						return !ignore(e.getGetter());
					}
				});
	}

	default Fields getSetterFields(Class<?> clazz, boolean useSuperClass, Field parentField) {
		return MapperUtils.getMapper()
				.getFields(ProxyUtils.getFactory().getUserClass(clazz), useSuperClass, parentField)
				.accept(SETTER_ACCEPT).accept(new Accept<Field>() {

					@Override
					public boolean accept(Field e) {
						return !ignore(e.getSetter());
					}
				});
	}
}
