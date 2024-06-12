package io.basc.framework.mapper.stereotype;

import java.util.List;
import java.util.function.Function;

import io.basc.framework.util.Assert;
import io.basc.framework.util.SimpleNamed;
import io.basc.framework.util.collect.LinkedMultiValueMap;
import io.basc.framework.util.collect.MultiValueMap;
import io.basc.framework.util.element.Elements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultMapping<T extends FieldDescriptor> extends SimpleNamed implements Mapping<T> {
	private MultiValueMap<String, T> elementMap;

	public DefaultMapping() {
	}

	public <S extends FieldDescriptor> DefaultMapping(Mapping<? extends S> mapping,
			Function<? super S, ? extends T> converter) {
		Assert.requiredArgument(mapping != null, "mapping");
		Assert.requiredArgument(converter != null, "converter");
		setName(mapping.getName());
		setAliasNames(mapping.getAliasNames());
		MultiValueMap<String, T> propertyMap = new LinkedMultiValueMap<>();
		for (S field : mapping.getElements()) {
			T property = converter.apply(field);
			propertyMap.add(property.getName(), property);
		}
		setElementMap(propertyMap);
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
