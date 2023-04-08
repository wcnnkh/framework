package io.basc.framework.value;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;

public final class Values implements Elements<Value> {
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

	protected final Value[] array;

	public Values(Value... array) {
		Assert.requiredArgument(array != null, "array");
		this.array = array;
	}

	@Override
	public Value first() {
		return array[0];
	}

	@Override
	public Value last() {
		return array[array.length - 1];
	}

	@Override
	public boolean isEmpty() {
		return array.length == 0;
	}

	@Override
	public List<Value> toList() {
		return Arrays.asList(array);
	}

	@Override
	public Iterator<Value> iterator() {
		return toList().iterator();
	}

	@Override
	public Stream<Value> stream() {
		return Stream.of(array);
	}
}
