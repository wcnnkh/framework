package run.soeasy.framework.core.collection;

import java.util.function.Function;
import java.util.function.Predicate;

public class EmptyProvider<S> extends EmptyElements<S> implements Provider<S> {
	private static final long serialVersionUID = 1L;
	static final Provider<?> EMPTY_PROVIDER = new EmptyProvider<>();

	public void reload() {
	}

	@Override
	public Provider<S> filter(Predicate<? super S> predicate) {
		return this;
	}

	@Override
	public <U> Provider<U> map(Function<? super S, ? extends U> mapper) {
		return Provider.empty();
	}
}