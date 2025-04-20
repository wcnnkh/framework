package run.soeasy.framework.core.convert;

import lombok.NonNull;
import run.soeasy.framework.core.exe.Function;
import run.soeasy.framework.core.exe.Supplier.MappedSupplier;

public class MappedSource<T, W extends Source> extends MappedSupplier<Object, T, ConversionException, W> {

	public MappedSource(@NonNull W source,
			@NonNull Function<? super Object, ? extends T, ? extends ConversionException> mapper) {
		super(source, mapper);
	}
}
