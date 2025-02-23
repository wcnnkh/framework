package io.basc.framework.core.convert;

import io.basc.framework.util.function.Function;
import io.basc.framework.util.function.Supplier.MappedSupplier;
import lombok.NonNull;

public class MappedSource<T, W extends Source> extends MappedSupplier<Object, T, ConversionException, W> {

	public MappedSource(@NonNull W source,
			@NonNull Function<? super Object, ? extends T, ? extends ConversionException> mapper) {
		super(source, mapper);
	}
}
