package scw.mapper;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import scw.lang.Nullable;
import scw.util.Accept;

public interface Fields extends Iterable<Field> {
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
	 * @return
	 */
	Fields duplicateRemoval();
	
	/**
	 * 可共享的
	 * @return
	 */
	Fields shared();
	
	int size();
	
	Fields accept(Accept<Field> accept);
	
	Fields accept(FieldFeature ...features);
	
	Fields exclude(Accept<Field> accept);
	
	Fields exclude(FieldFeature ...features);
	
	Fields exclude(Collection<String> names);
	
	void test(Object instance, @Nullable FieldTest test) throws IllegalArgumentException;
	
	Map<String, Object> getValueMap(Object instance);
	
	Map<String, Object> getValueMap(Object instance, boolean nullable);
}
