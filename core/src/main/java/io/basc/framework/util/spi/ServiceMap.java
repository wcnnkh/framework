package io.basc.framework.util.spi;

import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

import io.basc.framework.util.comparator.TypeComparator;
import io.basc.framework.util.register.container.ElementRegistration;
import io.basc.framework.util.register.container.MultiValueMapContainer;
import lombok.NonNull;

public class ServiceMap<V> extends
		MultiValueMapContainer<Class<?>, V, TreeSet<ElementRegistration<V>>, Services<V>, TreeMap<Class<?>, Services<V>>> {

	public ServiceMap() {
		this((key) -> new Services<>());
	}

	public ServiceMap(@NonNull Function<? super Class<?>, ? extends Services<V>> valuesCreator) {
		super(() -> new TreeMap<>(TypeComparator.DEFAULT), valuesCreator);
	}
}
