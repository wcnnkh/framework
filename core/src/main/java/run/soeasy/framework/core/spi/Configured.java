package run.soeasy.framework.core.spi;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Registration;

public interface Configured<S> extends Include<S>, Receipt {

	@Override
	default Configured<S> and(Registration registration) {
		return new AndConfigured<>(this, registration);
	}

	public static final Configured<?> FAILURE_CONFIGURED = new Included<>(true, false, null);

	@SuppressWarnings("unchecked")
	public static <S> Configured<S> failure() {
		return (Configured<S>) FAILURE_CONFIGURED;
	}

	@Override
	default <U> Configured<U> map(boolean resize,
			@NonNull Function<? super Stream<S>, ? extends Stream<U>> converter) {
		return new ConvertedConfigured<>(this, resize, converter);
	}
}
