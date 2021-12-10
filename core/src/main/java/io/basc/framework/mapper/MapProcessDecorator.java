package io.basc.framework.mapper;

import io.basc.framework.util.stream.Processor;

public class MapProcessDecorator<S, T, E extends Throwable> implements Processor<S, T, E> {
	private final Mapper<S, ? extends E> mapper;
	private final Processor<S, ? extends T, ? extends E> processor;
	private final Class<? extends T> type;

	public MapProcessDecorator(Mapper<S, ? extends E> mapper, Processor<S, ? extends T, ? extends E> processor,
			Class<? extends T> type) {
		this.mapper = mapper;
		this.processor = processor;
		this.type = type;
	}

	@Override
	public T process(S source) throws E {
		if (mapper.isRegistred(type)) {
			return mapper.process(type, source);
		}
		return processor.process(source);
	}
}
