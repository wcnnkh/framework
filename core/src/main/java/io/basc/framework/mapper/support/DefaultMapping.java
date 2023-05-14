package io.basc.framework.mapper.support;

import java.util.List;
import java.util.function.Function;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.LinkedMultiValueMap;
import io.basc.framework.util.MultiValueMap;
import lombok.Data;

@Data
public class DefaultMapping<T extends Field> implements Mapping<T> {
	private String name;
	private Elements<String> aliasNames;
	private MultiValueMap<String, T> elementMap;

	public DefaultMapping() {
	}

	public <S extends Field> DefaultMapping(Mapping<? extends S> mapping, Function<? super S, ? extends T> converter) {
		Assert.requiredArgument(mapping != null, "mapping");
		Assert.requiredArgument(converter != null, "converter");
		this.name = mapping.getName();
		this.aliasNames = mapping.getAliasNames();
		MultiValueMap<String, T> propertyMap = new LinkedMultiValueMap<>();
		for (S field : mapping.getElements()) {
			T property = converter.apply(field);
			propertyMap.add(property.getName(), property);
		}
		setElementMap(propertyMap);
	}

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
