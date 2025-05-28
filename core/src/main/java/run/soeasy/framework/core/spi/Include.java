package run.soeasy.framework.core.spi;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;
import run.soeasy.framework.core.exchange.Registration;

public interface Include<S> extends Registration, Provider<S> {

	@Override
	default <U> Include<U> convert(boolean resize,
			@NonNull Function<? super Stream<S>, ? extends Stream<U>> converter) {
		return new ConvertedInclude<>(this, resize, converter);
	}

	@Override
	default Include<S> and(Registration registration) {
		return new AndInclude<>(this, registration);
	}
}
