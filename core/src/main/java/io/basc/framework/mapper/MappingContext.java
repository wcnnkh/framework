package io.basc.framework.mapper;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.attribute.SimpleAttributes;
import io.basc.framework.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public final class MappingContext<T extends Field> extends SimpleAttributes<String, Value>
		implements ParentDiscover<MappingContext<T>> {
	private final ResolvableType source;
	private final Mapping<? extends T> mapping;
	private final T context;
	private final MappingContext<T> parent;

	public MappingContext(ResolvableType source, Mapping<T> mapping, T context) {
		this(source, mapping, context, null);
	}

	public MappingContext(ResolvableType source, Mapping<T> mapping, T context, MappingContext<T> parent) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(mapping != null, "mapping");
		Assert.requiredArgument(context != null, "context");
		this.source = source;
		this.mapping = mapping;
		this.context = context;
		this.parent = parent;
	}
}
