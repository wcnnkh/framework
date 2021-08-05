package scw.util.page;

import java.util.function.Function;

public class MapperPageableProcessor<K, S, T> implements
		PageableProcessor<K, T> {
	private final Function<? super S, ? extends T> mapper;
	private final PageableProcessor<K, S> processor;

	public MapperPageableProcessor(PageableProcessor<K, S> processor,
			Function<? super S, ? extends T> mapper) {
		this.processor = processor;
		this.mapper = mapper;
	}

	@Override
	public Pageable<K, T> process(K start, long count) {
		return processor.process(start, count).map(mapper);
	}

}
