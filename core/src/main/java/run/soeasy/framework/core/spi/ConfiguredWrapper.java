package run.soeasy.framework.core.spi;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.ReceiptWrapper;
import run.soeasy.framework.core.exchange.Registration;

@FunctionalInterface
public interface ConfiguredWrapper<S, W extends Configured<S>>
		extends Configured<S>, IncludeWrapper<S, W>, ReceiptWrapper<W> {
	@Override
	default <U> Configured<U> convert(boolean resize,
			@NonNull Function<? super Stream<S>, ? extends Stream<U>> converter) {
		return getSource().convert(resize, converter);
	}

	@Override
	default Configured<S> and(Registration registration) {
		return getSource().and(registration);
	}
}
