package run.soeasy.framework.core.spi;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;
import run.soeasy.framework.core.collection.ProviderWrapper;
import run.soeasy.framework.core.exchange.Receipted;

public class Included<S> extends Receipted implements Configured<S>, ProviderWrapper<S, Provider<S>> {
	private static final long serialVersionUID = 1L;
	private final Provider<S> source;

	public Included(boolean done, boolean success, Throwable cause) {
		this(done, success, cause, Provider.empty());
	}

	public Included(boolean done, boolean success, Throwable cause, Provider<S> source) {
		super(done, success, cause);
		this.source = source;
	}

	@Override
	public Provider<S> getSource() {
		return source;
	}

	@Override
	public <U> Configured<U> map(boolean resize, @NonNull Function<? super Stream<S>, ? extends Stream<U>> converter) {
		return Configured.super.map(resize, converter);
	}
}