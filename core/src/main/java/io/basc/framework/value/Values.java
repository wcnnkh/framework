package io.basc.framework.value;

import java.util.Arrays;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ElementsWrapper;

public class Values extends ElementsWrapper<Value, Elements<Value>> implements Elements<Value> {
	public static Values of(Object... args) {
		Assert.requiredArgument(args != null, "args");
		Value[] values = new Value[args.length];
		for (int i = 0; i < args.length; i++) {
			values[i] = new AnyValue(args[i]);
		}
		return new Values(values);
	}

	public static Values of(TypeDescriptor[] typeDescriptors, Object[] args) {
		Assert.requiredArgument(typeDescriptors != null, "typeDescriptors");
		Assert.requiredArgument(args != null, "args");
		Assert.requiredArgument(typeDescriptors.length != args.length, "The number of parameters is inconsistent");
		Value[] values = new Value[typeDescriptors.length];
		for (int i = 0; i < typeDescriptors.length; i++) {
			values[i] = new AnyValue(args[i], typeDescriptors[i]);
		}
		return new Values(values);
	}

	public Values(Value... values) {
		this(Elements.of(Arrays.asList(values)));
	}

	public Values(Elements<Value> elements) {
		super(elements);
	}
}
