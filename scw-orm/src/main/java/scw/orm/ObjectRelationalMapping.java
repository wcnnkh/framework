package scw.orm;

import java.util.Collection;

import scw.aop.support.ProxyUtils;
import scw.lang.Nullable;
import scw.mapper.Field;
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
public interface ObjectRelationalMapping extends EntityNameMapping{
	static final Accept<Field> ACCEPT = FieldFeature.EXISTING_GETTER_FIELD.and(FieldFeature.EXISTING_SETTER_FIELD)
			.and(FieldFeature.IGNORE_STATIC).and(FieldFeature.GETTER_IGNORE_TRANSIENT)
			.and(FieldFeature.SETTER_IGNORE_TRANSIENT);

	/**
	 * 字段名称
	 * 
	 * @param field
	 * @return
	 */
	String getName(Field field);

	/**
	 * 字段可用于setter的名称
	 * 
	 * @param field
	 * @return
	 */
	Collection<String> getSetterNames(Field field);
	
	/**
	 * 字段描述
	 * 
	 * @param field
	 * @return
	 */
	@Nullable
	String getDescription(Field field);

	/**
	 * 是否是主键
	 * 
	 * @param field
	 * @return
	 */
	boolean isPrimaryKey(Field field);

	/**
	 * 是否是一个实体类
	 * 
	 * @param field
	 * @return
	 */
	boolean isEntity(Field field);

	boolean ignore(Field field);

	/**
	 * 获取对象的全部字段
	 * 
	 * @param clazz
	 * @return
	 */
	default Fields getFields(Class<?> clazz) {
		return MapperUtils.getMapper().getFields(ProxyUtils.getFactory().getUserClass(clazz)).accept(ACCEPT)
				.accept(new Accept<Field>() {

					@Override
					public boolean accept(Field e) {
						return !ignore(e);
					}
				});
	}

	/**
	 * 获取字段的全部主键
	 * 
	 * @param clazz
	 * @return
	 */
	default Fields getPrimaryKeys(Class<?> clazz) {
		return getFields(clazz).accept(new Accept<Field>() {
			@Override
			public boolean accept(Field e) {
				return isPrimaryKey(e);
			}
		});
	}

	/**
	 * 获取所有字段，不包含实体对象
	 * 
	 * @param clazz
	 * @return
	 */
	default Fields getColumns(Class<?> clazz) {
		return getFields(clazz).accept(new Accept<Field>() {
			@Override
			public boolean accept(Field e) {
				return !isEntity(e);
			}
		});
	}
}
