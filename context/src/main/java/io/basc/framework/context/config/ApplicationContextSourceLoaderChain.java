package io.basc.framework.context.config;

import java.util.Iterator;

import io.basc.framework.util.collections.Elements;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
public class ApplicationContextSourceLoaderChain<S, T> implements ApplicationContextSourceLoader<S, T> {
	private final Iterator<? extends ApplicationContextSourceLoadExtender<S, T>> iterator;
	private ApplicationContextSourceLoader<? super S, T> nextChain;

	@Override
	public Elements<T> load(ConfigurableApplicationContext context, S source) {
		if (iterator.hasNext()) {
			return iterator.next().load(context, source, this);
		} else if (nextChain != null) {
			return nextChain.load(context, source);
		}
		return Elements.empty();
	}

}
