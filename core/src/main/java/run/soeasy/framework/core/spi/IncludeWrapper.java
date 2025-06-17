package run.soeasy.framework.core.spi;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.collection.ProviderWrapper;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.RegistrationWrapper;

@FunctionalInterface
public interface IncludeWrapper<S, W extends Include<S>>
		extends Include<S>, RegistrationWrapper<W>, ProviderWrapper<S, W> {

	@Override
	default <U> Include<U> map(boolean resize, @NonNull Function<? super Stream<S>, ? extends Stream<U>> converter) {
		return getSource().map(resize, converter);
	}

	@Override
	default Include<S> and(Registration registration) {
		return getSource().and(registration);
	}
}