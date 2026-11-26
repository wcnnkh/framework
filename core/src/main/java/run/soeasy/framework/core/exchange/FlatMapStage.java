package run.soeasy.framework.core.exchange;

import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class FlatMapStage<S, T, P extends Stage<S>> implements StageWrapper<T, Stage<T>> {
	private final P parent;
	private final Function<? super S, ? extends Stage<T>> mapper;
	private volatile Stage<T> source;

	public Stage<T> getSource() {
		if (source == null) {
			synchronized (this) {
				if (source == null) {
					if (parent.isSuccess()) {
						source = mapper.apply(parent.getNow());
					} else {
						source = Signal.failure(parent.trace().cause());
					}
				}
			}
		}
		return source;
	}
}
