package io.basc.framework.mapper;

import java.util.function.Function;

import io.basc.framework.core.DecorationMembers;
import io.basc.framework.core.Members;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.util.element.Elements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ObjectMapping<T extends FieldDescriptor, R extends ObjectMapping<T, R>> extends DecorationMembers<T, R>
		implements Mapping<T> {
	private volatile String name;
	private Elements<String> aliasNames;

	private final Function<? super Members<T>, ? extends R> structureDecorator = (members) -> {
		ObjectMapping<T, R> mapping = new ObjectMappingWrapper<>(members, getStructureDecorator());
		mapping.name = this.name;
		mapping.aliasNames = this.aliasNames;
		return getObjectMappingDecorator().apply(mapping);
	};

	public ObjectMapping(Class<?> source, Function<? super Class<?>, ? extends Elements<T>> processor) {
		super(source, processor);
	}

	public ObjectMapping(Members<T> members) {
		super(members);
	}

	public ObjectMapping(ResolvableType source, Elements<T> elements,
			Function<? super ResolvableType, ? extends Elements<T>> processor) {
		super(source, elements, processor);
	}

	public Elements<String> getAliasNames() {
		return aliasNames == null ? Elements.empty() : aliasNames;
	}

	@Override
	public Elements<T> getElements() {
		return super.getElements();
	}

	public String getName() {
		if (name == null) {
			synchronized (this) {
				if (name == null) {
					name = getSource().getRawClass().getName();
				}
			}
		}
		return name;
	}

	public abstract Function<? super ObjectMapping<T, R>, ? extends R> getObjectMappingDecorator();

	@Override
	public final Function<? super Members<T>, ? extends R> getStructureDecorator() {
		return structureDecorator;
	}

	public void setAliasNames(Elements<String> aliasNames) {
		this.aliasNames = aliasNames;
	}

	public void setName(String name) {
		this.name = name;
	}

}
