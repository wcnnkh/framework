package run.soeasy.framework.core.spi;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.collection.ConvertedProvider;

public class ConvertedInclude<S, T, W extends Include<S>> extends ConvertedProvider<S, T, W> implements Include<T> {

	public ConvertedInclude(@NonNull W target, boolean resize,
			@NonNull Function<? super Stream<S>, ? extends Stream<T>> converter) {
		super(target, resize, converter);
	}

	@Override
	public boolean cancel() {
		return getTarget().cancel();
	}

	@Override
	public boolean isCancellable() {
		return getTarget().isCancellable();
	}

	@Override
	public boolean isCancelled() {
		return getTarget().isCancelled();
	}

	@Override
	public <U> Include<U> convert(boolean resize, Function<? super Stream<T>, ? extends Stream<U>> converter) {
		return Include.super.convert(resize, converter);
	}

}