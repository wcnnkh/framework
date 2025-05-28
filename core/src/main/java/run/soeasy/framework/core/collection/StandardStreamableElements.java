package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class StandardStreamableElements<E, W extends Streamable<E>> implements StreamableElementsWrapper<E, W> {
	private final W source;

	@Override
	public Iterator<E> iterator() {
		List<E> list = source.collect(Collectors.toList());
		return list.iterator();
	}

	@Override
	public Stream<E> stream() {
		return source.stream();
	}
}