package io.basc.framework.mapper;

import io.basc.framework.util.Assert;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.attribute.SimpleAttributes;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public final class MappingContext extends SimpleAttributes<String, Value> implements ParentDiscover<MappingContext> {
	private final Mapping<? extends Member> mapping;
	private final Member field;
	private final MappingContext parent;

	public MappingContext(Mapping<? extends Member> mapping, Member field, MappingContext parent) {
		Assert.requiredArgument(mapping != null, "mapping");
		Assert.requiredArgument(field != null, "field");
		this.mapping = mapping;
		this.field = field;
		this.parent = parent;
	}

	/**
	 * 获取上下文中的所有字段
	 * 
	 * @return
	 */
	public Elements<Member> getContextFields() {
		return Elements.singleton(field).concat(parents().map((e) -> e.getField()));
	}
}
