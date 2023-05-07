package io.basc.framework.mapper;

import java.util.function.Function;

import io.basc.framework.core.DecorationMembers;
import io.basc.framework.core.Members;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.util.Elements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class FieldMapping<T extends Field, R extends FieldMapping<T, R>> extends DecorationMembers<T, R>
		implements Mapping<T> {
	public FieldMapping(ResolvableType source, Elements<T> elements,
			Function<? super ResolvableType, ? extends Elements<T>> processor) {
		super(source, elements, processor);
	}

	public FieldMapping(Class<?> source, Function<? super Class<?>, ? extends Elements<T>> processor) {
		super(source, processor);
	}

	public FieldMapping(Members<T> members) {
		super(members);
	}

	private final Function<? super Members<T>, ? extends R> structureDecorator = (members) -> {
		FieldMapping<T, R> mapping = new DefaultStructureMapping<>(members, getStructureDecorator());
		return getObjectMappingDecorator().apply(mapping);
	};

	public abstract Function<? super FieldMapping<T, R>, ? extends R> getObjectMappingDecorator();

	@Override
	public final Function<? super Members<T>, ? extends R> getStructureDecorator() {
		return structureDecorator;
	}

	@Override
	public Elements<T> getElements() {
		return super.getElements();
	}
}
