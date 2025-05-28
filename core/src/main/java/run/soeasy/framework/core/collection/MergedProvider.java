package run.soeasy.framework.core.collection;

import java.util.function.Function;

public class MergedProvider<S, T extends Provider<? extends S>> implements ReloadableElementsWrapper<S, Elements<S>> {
	private final Elements<Provider<? extends S>> elements;

	public MergedProvider(Elements<Provider<? extends S>> elements) {
		this.elements = elements;
	}

	@Override
	public void reload() {
		elements.forEach(Provider::reload);
	}

	@Override
	public Elements<S> getSource() {
		return elements.flatMap((e) -> e.map(Function.identity()));
	}

	@Override
	public Provider<S> concat(Provider<? extends S> serviceLoader) {
		return new MergedProvider<>(this.elements.concat(Elements.singleton(serviceLoader)));
	}
}