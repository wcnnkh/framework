package io.basc.framework.core.env;

import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.core.convert.transform.Mapping;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.KeyValues;

public interface PropertyMapping<T extends Property> extends Mapping<String, T>, KeyValues<String, T> {

	@Override
	default Stream<KeyValue<String, T>> stream() {
		return getElements().stream();
	}

	@Override
	default Iterator<KeyValue<String, T>> iterator() {
		return getElements().iterator();
	}

	@Override
	default boolean isEmpty() {
		return KeyValues.super.isEmpty();
	}

}
