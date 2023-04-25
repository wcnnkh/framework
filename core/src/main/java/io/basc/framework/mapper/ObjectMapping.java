package io.basc.framework.mapper;

import java.util.function.Function;

import io.basc.framework.core.DecorationStructure;
import io.basc.framework.core.DefaultStructure;
import io.basc.framework.core.Members;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;

public abstract class ObjectMapping<T extends Field, R extends ObjectMapping<T, R>> extends DecorationStructure<T, R>
		implements Mapping<T> {

	private Elements<String> aliasNames;

	private String name;

	private final Function<? super DefaultStructure<T>, ? extends R> structureDecorator = (members) -> {
		ObjectMapping<T, R> mapping = new SimpleObjectMapping<>(members, getStructureDecorator());
		mapping.name = this.name;
		mapping.aliasNames = this.aliasNames;
		return getObjectMappingDecorator().apply(mapping);
	};

	public ObjectMapping(Class<?> source, Function<? super Class<?>, ? extends Elements<T>> processor) {
		super(source, processor);
	}

	public ObjectMapping(DefaultStructure<T> members) {
		super(members);
	}

	public ObjectMapping(Members<T> members, Function<? super ResolvableType, ? extends Elements<T>> processor) {
		super(members, processor);
	}

	public ObjectMapping(ResolvableType source, Function<? super ResolvableType, ? extends Elements<T>> processor) {
		super(source, processor);
	}

	@Override
	public Elements<String> getAliasNames() {
		if (aliasNames != null) {
			return aliasNames;
		}

		String name = getSource().getRawClass().getSimpleName();
		name = StringUtils.toLowerCase(name, 0, 1);
		name = StringUtils.humpNamingReplacement(name, "_");
		return Elements.singleton(name);
	}

	@Override
	public String getName() {
		if (StringUtils.isNotEmpty(name)) {
			return name;
		}

		return getSource().getRawClass().getSimpleName();
	}

	public abstract Function<? super ObjectMapping<T, R>, ? extends R> getObjectMappingDecorator();

	@Override
	public final Function<? super DefaultStructure<T>, ? extends R> getStructureDecorator() {
		return structureDecorator;
	}
}
