package io.basc.framework.core.convert;

import io.basc.framework.util.function.Optional;

public interface Value2<T> extends Optional<T, ConversionException> {
	Source getSource();
}
