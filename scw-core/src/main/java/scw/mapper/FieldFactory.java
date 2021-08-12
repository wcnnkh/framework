package scw.mapper;

public interface FieldFactory {
	/**
	 * 获取珍上类所有的字段(包含父类)
	 * @param entityClass
	 * @return
	 */
	default Fields getFields(Class<?> entityClass) {
		return getFields(entityClass, true, null);
	}

	/**
	 * 获取一个类的字段
	 * @param entityClass
	 * @param useSuperClass 是否包含父类
	 * @return
	 */
	default Fields getFields(Class<?> entityClass, boolean useSuperClass) {
		return getFields(entityClass, useSuperClass, null);
	}

	/**
	 * 获取一个类所有的字段(包含父类)
	 * @param entityClass
	 * @param parentField 父级字段
	 * @return
	 */
	default Fields getFields(Class<?> entityClass, Field parentField) {
		return getFields(entityClass, true, parentField);
	}

	/**
	 * 获取一个类的字段
	 * @param entityClass
	 * @param useSuperClass 是否包含父类
	 * @param parentField 父级字段
	 * @return
	 */
	Fields getFields(Class<?> entityClass, boolean useSuperClass, Field parentField);
}
