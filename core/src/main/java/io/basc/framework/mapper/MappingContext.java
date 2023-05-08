package io.basc.framework.mapper;

import io.basc.framework.util.Assert;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.attribute.SimpleAttributes;
import io.basc.framework.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public final class MappingContext extends SimpleAttributes<String, Value> implements ParentDiscover<MappingContext> {
	private final Mapping<? extends Field> mapping;
	private final Field context;
	private final MappingContext parent;

	public MappingContext(Mapping<? extends Field> mapping, Field context, MappingContext parent) {
		Assert.requiredArgument(mapping != null, "mapping");
		Assert.requiredArgument(context != null, "context");
		this.mapping = mapping;
		this.context = context;
		this.parent = parent;
	}
}
