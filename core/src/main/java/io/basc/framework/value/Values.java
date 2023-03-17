package io.basc.framework.value;

import java.util.Arrays;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Cursor;
import io.basc.framework.util.ResultSet;

public class Values implements ResultSet<Value> {
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

	protected final ResultSet<Value> resultSet;

	public Values(Value... values) {
		Assert.requiredArgument(values != null, "values");
		this.resultSet = ResultSet.of(Arrays.asList(values));
	}

	public Values(ResultSet<Value> resultSet) {
		Assert.requiredArgument(resultSet != null, "resultSet");
		this.resultSet = resultSet;
	}

	@Override
	public Value first() {
		return resultSet.first();
	}

	@Override
	public Value last() {
		return resultSet.last();
	}

	@Override
	public List<Value> toList() {
		return resultSet.toList();
	}

	public Object[] toArray() {
		List<Value> values = resultSet.toList();
		Object[] args = new Object[values.size()];
		for (int i = 0; i < args.length; i++) {
			args[i] = values.get(i).getSource();
		}
		return args;
	}

	@Override
	public Cursor<Value> iterator() {
		return resultSet.iterator();
	}
}
