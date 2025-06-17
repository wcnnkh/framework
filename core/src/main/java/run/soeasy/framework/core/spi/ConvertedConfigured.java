package run.soeasy.framework.core.spi;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;

public class ConvertedConfigured<S, T, W extends Configured<S>> extends ConvertedInclude<S, T, W>
		implements Configured<T> {

	public ConvertedConfigured(@NonNull W target, boolean resize,
			@NonNull Function<? super Stream<S>, ? extends Stream<T>> converter) {
		super(target, resize, converter);
	}

	@Override
	public <U> Configured<U> map(boolean resize, Function<? super Stream<T>, ? extends Stream<U>> converter) {
		return Configured.super.map(resize, converter);
	}

	@Override
	public Throwable cause() {
		return getTarget().cause();
	}

	@Override
	public boolean isDone() {
		return getTarget().isDone();
	}

	@Override
	public boolean isSuccess() {
		return getTarget().isSuccess();
	}

}