package scw.orm.support;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ObjectOperations {
	/**
	 * 移除指定字段
	 * 
	 * @param obj
	 * @param excludeNames
	 * @return
	 */
	Map<String, Object> getColumnValueMapExcludeName(Object obj, Collection<String> excludeNames);

	/**
	 * 只保留指定字段
	 * 
	 * @param obj
	 * @param effectiveNames
	 * @return
	 */
	Map<String, Object> getColumnValueMapEffectiveName(Object obj, Collection<String> effectiveNames);

	Map<String, Object> getColumnValueMap(Object obj);

	/**
	 * 移除指定字段
	 * 
	 * @param objs
	 * @param excludeNames
	 * @return
	 */
	List<Map<String, Object>> getColumnValueListMapExcludeName(Collection<?> objs, Collection<String> excludeNames);

	/**
	 * 保留指定字段
	 * 
	 * @param objs
	 * @param effectiveNames
	 * @return
	 */
	List<Map<String, Object>> getColumnValueListMapEffectiveName(Collection<?> objs, Collection<String> effectiveNames);

	List<Map<String, Object>> getColumnValueListMap(Collection<?> objs);

	<E> List<E> getColumnValueList(Collection<?> objs, String name);

	<E> List<E> getColumnValueListByFirstPrimary(Collection<?> objs);

	<K, V> Map<K, ? extends V> toMap(Collection<? extends V> objs, String name);

	<K, V> Map<K, ? extends V> toMapByFirstPrimary(Collection<? extends V> objs);

	void verify(Object obj);

	void verify(Collection<Object> objs);
}
