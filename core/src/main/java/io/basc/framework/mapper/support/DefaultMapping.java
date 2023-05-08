package io.basc.framework.mapper.support;

import java.util.List;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.util.Elements;
import io.basc.framework.util.MultiValueMap;
import lombok.Data;

@Data
public class DefaultMapping<T extends Field> implements Mapping<T> {
	private String name;
	private Elements<String> aliasNames;
	private MultiValueMap<String, T> elementMap;

	public Elements<String> getAliasNames() {
		return aliasNames == null ? Elements.empty() : aliasNames;
	}

	public Elements<T> getElements() {
		return elementMap == null ? Elements.empty()
				: Elements.of(() -> elementMap.values().stream().flatMap((e) -> e.stream()));
	}

	@Override
	public Elements<T> getElements(String name) {
		if (elementMap == null) {
			return Elements.empty();
		}

		List<T> list = elementMap.get(name);
		return list == null ? Elements.empty() : Elements.of(list);
	}
}
