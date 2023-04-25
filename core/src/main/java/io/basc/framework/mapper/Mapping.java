package io.basc.framework.mapper;

import io.basc.framework.util.Elements;
import io.basc.framework.util.LinkedMultiValueMap;
import io.basc.framework.util.MultiValueMap;
import io.basc.framework.value.Value;

/**
 * 映射
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface Mapping<T extends Field> {
	String getName();

	Elements<String> getAliasNames();

	Elements<T> getElements();

	/**
	 * 获取字段的值
	 * 
	 * @param instance
	 * @return 可能存在相同的字段名
	 */
	default MultiValueMap<String, Value> getMultiValueMap(Value source) {
		MultiValueMap<String, Value> map = new LinkedMultiValueMap<>();
		for (Field field : this.getElements()) {
			if (!field.isSupportGetter()) {
				continue;
			}

			Value value = field.get(source);
			if (value == null) {
				continue;
			}

			map.add(field.getName(), value);
		}
		return map;
	}
}
