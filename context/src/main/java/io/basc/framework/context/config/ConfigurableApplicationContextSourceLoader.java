package io.basc.framework.context.config;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.Merger;
import io.basc.framework.util.select.Selector;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurableApplicationContextSourceLoader<S, T, E extends ApplicationContextSourceLoader<S, T>, R extends ApplicationContextSourceLoadExtender<S, T>>
		extends ConfigurableServices<E>
		implements ApplicationContextSourceLoader<S, T>, ApplicationContextSourceLoadExtender<S, T> {
	private final ConfigurableApplicationContextSourceLoadExtender<S, T, R> extender = new ConfigurableApplicationContextSourceLoadExtender<>();
	private Selector<Elements<T>> selector = Merger.global();

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		super.configure(serviceLoaderFactory);
		extender.configure(serviceLoaderFactory);
	}

	@Override
	public Elements<T> load(ConfigurableApplicationContext context, S source) {
		return load(context, source, null);
	}

	@Override
	public Elements<T> load(ConfigurableApplicationContext context, S source,
			ApplicationContextSourceLoader<? super S, T> chain) {
		Elements<T> elements = selector.apply(getServices().map((e) -> e.load(context, source)));
		Elements<T> extendElements = extender.load(context, source, chain);
		return selector.apply(Elements.forArray(elements, extendElements));
	}
}
