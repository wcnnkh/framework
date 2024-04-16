package io.basc.framework.context.config;

import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
public class ApplicationContextSourceProcessChain<T> implements ApplicationContextSourceProcessor<T> {
	@NonNull
	private final Iterator<? extends ApplicationContextSourceProcessExtender<T>> iterator;
	private ApplicationContextSourceProcessor<? super T> nextChain;

	@Override
	public void process(ConfigurableApplicationContext context, T source) {
		if (iterator.hasNext()) {
			iterator.next().process(context, source, this);
		} else if (nextChain != null) {
			nextChain.process(context, source);
		}
	}
}
