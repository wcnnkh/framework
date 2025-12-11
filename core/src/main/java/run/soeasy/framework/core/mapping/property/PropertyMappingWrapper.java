package run.soeasy.framework.core.mapping.property;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.streaming.MappingWrapper;
import run.soeasy.framework.core.streaming.Streamable;

@FunctionalInterface
public interface PropertyMappingWrapper<E extends PropertyDescriptor, W extends PropertyMapping<E>>
		extends PropertyMapping<E>, MappingWrapper<String, E, W> {
	@Override
	default Streamable<E> elements() {
		return getSource().elements();
	}

	@Override
	default Class<?>[] getTypes(Function<? super E, ? extends Class<?>> typeMapper) {
		return getSource().getTypes(typeMapper);
	}

	@Override
	default Stream<KeyValue<String, E>> stream() {
		return getSource().stream();
	}

	@Override
	default PropertyMapping<E> toIndexed() {
		return getSource().toIndexed();
	}

	@Override
	default PropertyMapping<E> toIndexed(@NonNull Supplier<? extends List<KeyValue<String, E>>> collectionFactory) {
		return getSource().toIndexed(collectionFactory);
	}

	@Override
	default PropertyMapping<E> toMapped() {
		return getSource().toMapped();
	}

	@Override
	default PropertyMapping<E> toMapped(@NonNull BinaryOperator<E> mergeFunction) {
		return getSource().toMapped(mergeFunction);
	}

	@Override
	default PropertyMapping<E> toMapped(@NonNull BinaryOperator<E> mergeFunction,
			@NonNull Supplier<Map<String, E>> mapFactory) {
		return getSource().toMapped(mergeFunction, mapFactory);
	}

	@Override
	default PropertyMapping<E> toMultiMapped() {
		return getSource().toMultiMapped();
	}

	@Override
	default PropertyMapping<E> toMultiMapped(@NonNull Supplier<Map<String, Collection<E>>> mapFactory) {
		return getSource().toMultiMapped(mapFactory);
	}

	@Override
	default PropertyMapping<E> toMultiMapped(@NonNull Supplier<Map<String, Collection<E>>> mapFactory,
			@NonNull Supplier<Collection<E>> collectionFactory) {
		return getSource().toMultiMapped(mapFactory, collectionFactory);
	}
	
	@Override
	default Optional<E> uniqueProperty(@NonNull Object key) {
		return getSource().uniqueProperty(key);
	}
	
	@Override
	default PropertyMapping<E> reload() {
		return getSource().reload();
	}
}
