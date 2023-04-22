package io.basc.framework.mapper;

import java.util.function.Consumer;
import java.util.function.Function;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Members;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.Structure;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ObjectMapping<T extends Field> extends Structure<T> implements Mapping<T>, Consumer<T> {
	private String name;
	private Elements<String> aliasNames;
	private T parent;
	private int nameNestingDepth = -1;
	private String nameNestingConnector = "_";
	private final TypeDescriptor typeDescriptor;

	public ObjectMapping(TypeDescriptor typeDescriptor, Members<T> members) {
		super(members);
		Assert.requiredArgument(typeDescriptor != null, "typeDescriptor");
		this.typeDescriptor = typeDescriptor;
	}

	public ObjectMapping(TypeDescriptor typeDescriptor,
			Function<? super ResolvableType, ? extends Members<T>> processor) {
		super(typeDescriptor.getResolvableType(), processor);
		Assert.requiredArgument(typeDescriptor != null, "typeDescriptor");
		this.typeDescriptor = typeDescriptor;
	}

	public ObjectMapping(TypeDescriptor typeDescriptor, Structure<T> structure) {
		super(structure);
		Assert.requiredArgument(typeDescriptor != null, "typeDescriptor");
		this.typeDescriptor = typeDescriptor;
	}

	@Override
	public String getName() {
		if (StringUtils.isNotEmpty(name)) {
			return name;
		}

		return typeDescriptor.getType().getSimpleName();
	}

	@Override
	public Elements<String> getAliasNames() {
		if (aliasNames != null) {
			return aliasNames;
		}

		String name = typeDescriptor.getType().getSimpleName();
		name = StringUtils.toLowerCase(name, 0, 1);
		name = StringUtils.humpNamingReplacement(name, "_");
		return Elements.singleton(name);
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor;
	}

	@Override
	public Members<T> getMembers() {
		return super.getMembers().peek(this);
	}

	@Override
	public Structure<T> getSuperclass() {
		Structure<T> structure = super.getSuperclass();
		return structure == null ? null : structure.peek(this);
	}

	@Override
	public Elements<Structure<T>> getInterfaces() {
		Elements<Structure<T>> elements = super.getInterfaces();
		return elements == null ? null : elements.map((e) -> e.peek(this));
	}

	@Override
	public void accept(T t) {
		// TODO
	}
}
