package io.basc.framework.mapper;

import java.util.function.Function;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import lombok.Data;

@Data
public class ConvertibleMapping<S extends Element, T extends Element> implements Mapping<T> {
	private String name;
	private Elements<String> aliasNames;
	private final Mapping<S> sourceMapping;
	private final Function<? super Elements<S>, ? extends Elements<T>> converter;

	public ConvertibleMapping(Mapping<S> sourceMapping,
			Function<? super Elements<S>, ? extends Elements<T>> converter) {
		Assert.requiredArgument(sourceMapping != null, "sourceMapping");
		Assert.requiredArgument(converter != null, "converter");
		this.sourceMapping = sourceMapping;
		this.converter = converter;
	}

	@Override
	public Elements<String> getAliasNames() {
		return aliasNames == null ? sourceMapping.getAliasNames() : aliasNames;
	}

	@Override
	public Elements<T> getElements() {
		Elements<S> elements = sourceMapping.getElements();
		return converter.apply(elements);
	}

	@Override
	public Elements<T> getElements(String name) {
		Elements<S> elements = sourceMapping.getElements(name);
		return converter.apply(elements);
	}

	@Override
	public String getName() {
		return name == null ? sourceMapping.getName() : name;
	}
}
