package io.basc.framework.transform;

import java.util.function.Supplier;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.Value;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.function.StaticSupplier;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadOnlyProperty implements Property {
	private final int positionIndex;
	private final String name;
	private final Elements<String> aliasNames;
	private final Supplier<? extends Value> supplier;

	public ReadOnlyProperty(String name, Elements<String> aliasNames, Value value) {
		this(-1, name, aliasNames, new StaticSupplier<>(value));
	}

	public ReadOnlyProperty(int positionIndex, Value value) {
		this(positionIndex, null, null, new StaticSupplier<>(value));
	}

	@Override
	public Elements<String> getAliasNames() {
		return aliasNames == null ? Elements.empty() : aliasNames;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	public Value get() {
		Value value = supplier.get();
		return value == null ? Value.EMPTY : value;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return get().getTypeDescriptor();
	}

	@Override
	public Object getValue() {
		return get().getValue();
	}

	@Override
	public void setValue(Object value) {
		throw new UnsupportedOperationException(name);
	}
}
