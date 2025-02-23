package io.basc.framework.core.convert;

import io.basc.framework.util.function.Function;
import io.basc.framework.util.function.Supplier;
import io.basc.framework.util.function.Supplier.MappedSupplier;
import lombok.NonNull;

public class MappedSource<S, T, E extends Throwable, W extends Supplier<S, E>> extends MappedSupplier<S, T, E, W> {

	public MappedSource(@NonNull W source, @NonNull Function<? super S, ? extends T, ? extends E> mapper) {
		super(source, mapper);
	}

}
