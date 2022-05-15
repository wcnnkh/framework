package io.basc.framework.orm;

import io.basc.framework.aop.support.ProxyUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

public interface EntityOperations {
	default Class<?> getUserClass(Class<?> entityClass) {
		return ProxyUtils.getFactory().getUserClass(entityClass);
	}

	/**
	 * @see #save(Class, Object)
	 * @param entity
	 */
	default void save(Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		save(getUserClass(entity.getClass()), entity);
	}

	/**
	 * 判断是否存在，只判断主键
	 * 
	 * @see #getById(Class, Object...)
	 * @see #isPresent(Class, Object...)
	 * @param entityClass
	 * @param entity
	 * @return
	 */
	<T> boolean isPresent(Class<? extends T> entityClass, T entity);

	/**
	 * 判断是否存在
	 * 
	 * @param entityClass
	 * @param ids
	 * @return
	 */
	boolean isPresent(Class<?> entityClass, Object... ids);

	/**
	 * 保存
	 * 
	 * @param entityClass
	 * @param entity
	 */
	<T> void save(Class<? extends T> entityClass, T entity);

	/**
	 * @see #saveIfAbsent(Class, Object)
	 * @param entity
	 * @return
	 */
	default boolean saveIfAbsent(Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		return saveIfAbsent(getUserClass(entity.getClass()), entity);
	}

	/**
	 * 如果不存在就保存
	 * 
	 * @param entityClass
	 * @param entity
	 * @return 如果实体已经存在返回false
	 */
	default <T> boolean saveIfAbsent(Class<? extends T> entityClass, T entity) {
		// 这种实现线程安全，如果能实现原子性的实现更好
		if (isPresent(entityClass, entity)) {
			return false;
		}
		save(entityClass, entity);
		return true;
	}

	/**
	 * @see #deleteById(Class, Object...)
	 * @see #delete(Class, Object)
	 * @param entity
	 * @return
	 */
	default boolean delete(Object entity) {
		if (entity == null) {
			return false;
		}

		return delete(getUserClass(entity.getClass()), entity);
	}

	/**
	 * 删除数据,根据主键删除
	 * 
	 * @see #deleteById(Class, Object...)
	 * 
	 * @param entityClass
	 * @param entity
	 * @return 如果数据不存在返回false
	 */
	<T> boolean delete(Class<? extends T> entityClass, T entity);

	/**
	 * 删除数据
	 * 
	 * @param entityClass
	 * @param ids
	 * @return 如果数据不存在就返回false
	 */
	boolean deleteById(Class<?> entityClass, Object... ids);

	/**
	 * 更新数据
	 * 
	 * @see #update(Class, Object)
	 * @param entity
	 * @return
	 */
	default boolean update(Object entity) {
		if (entity == null) {
			return false;
		}

		return update(getUserClass(entity.getClass()), entity);
	}

	/**
	 * 更新
	 * 
	 * @param entityClass
	 * @param entity
	 * @return 如果数据不存在就返回false
	 */
	<T> boolean update(Class<? extends T> entityClass, T entity);

	/**
	 * @see #saveOrUpdate(Class, Object)
	 * @param entity
	 * @return
	 */
	default boolean saveOrUpdate(Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		return saveOrUpdate(getUserClass(entity.getClass()), entity);
	}

	/**
	 * 更新数据
	 * 
	 * @param entityClass
	 * @param entity
	 * @return 失败情况参考{@link #update(Class, Object)}
	 */
	default <T> boolean saveOrUpdate(Class<? extends T> entityClass, T entity) {
		if (saveIfAbsent(entityClass, entity)) {
			return true;
		}
		return update(entityClass, entity);
	}

	/**
	 * 获取数据
	 * 
	 * @param entityClass
	 * @param ids
	 * @return
	 */
	@Nullable
	<T> T getById(Class<? extends T> entityClass, Object... ids);
}
