package io.basc.framework.transform.strategy;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.ValueWrapper;
import io.basc.framework.core.convert.transform.ParameterDescriptor;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.attribute.SimpleAttributes;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PropertiesTransformContext extends SimpleAttributes<String, ValueWrapper>
		implements Properties, ParentDiscover<PropertiesTransformContext>, ParameterDescriptor, Cloneable {
	@NonNull
	private final Properties properties;
	@NonNull
	private final TypeDescriptor typeDescriptor;
	@NonNull
	private final Property property;
	private final PropertiesTransformContext parent;
	private String name;
	@NonNull
	private Elements<String> aliasNames = Elements.empty();
	private int positionIndex = -1;

	@Override
	public Elements<Property> getElements() {
		return Elements.singleton(property).concat(parents().map((e) -> e.getProperty()));
	}

	@Override
	public PropertiesTransformContext clone() {
		PropertiesTransformContext context = new PropertiesTransformContext(properties, typeDescriptor, property,
				parent);
		context.name = this.name;
		context.aliasNames = this.aliasNames;
		context.positionIndex = this.positionIndex;
		return context;
	}

	@Override
	public PropertiesTransformContext rename(String name) {
		PropertiesTransformContext context = clone();
		context.name = name;
		return context;
	}
}
