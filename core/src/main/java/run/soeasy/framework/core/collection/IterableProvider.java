package run.soeasy.framework.core.collection;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IterableProvider<S> implements ReloadableElementsWrapper<S, Elements<S>> {
	@NonNull
	private final Iterable<? extends S> source;

	@Override
	public Elements<S> getSource() {
		return Elements.of(source);
	}

	@Override
	public void reload() {
		if (source instanceof Reloadable) {
			((Reloadable) source).reload();
		}
	}
}