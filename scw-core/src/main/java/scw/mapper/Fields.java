package scw.mapper;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import scw.lang.Nullable;
import scw.util.Accept;

public interface Fields extends Iterable<Field> {
	/**
	 * 获取第一个字段
	 * 
	 * @return
	 */
	@Nullable
	Field first();

	@Nullable
	Field find(String name, @Nullable Type type);

	@Nullable
	Field findGetter(String name, @Nullable Type type);

	@Nullable
	Field findSetter(String name, @Nullable Type type);

	/**
	 * 去重
	 * 
	 * @return
	 */
	Fields duplicateRemoval();

	/**
	 * 可共享的
	 * 
	 * @return
	 */
	Fields shared();

	/**
	 * 获取字段数量，在非shared下字段的数量通过遍历获取的,所以推荐先调用shared再获取数量
	 * 
	 * @return
	 */
	int size();

	Fields accept(Accept<Field> accept);

	/**
	 * 排除一些字段
	 * 
	 * @param accept
	 * @return
	 */
	Fields exclude(Accept<Field> accept);

	/**
	 * 排除一些字段
	 * 
	 * @param names
	 * @return
	 */
	Fields exclude(Collection<String> names);

	void test(Object instance, @Nullable FieldTest test) throws IllegalArgumentException;

	Map<String, Object> getValueMap(Object instance);

	Map<String, Object> getValueMap(Object instance, boolean nullable);
}
