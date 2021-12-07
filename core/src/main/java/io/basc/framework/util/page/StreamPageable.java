package io.basc.framework.util.page;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.util.stream.StreamProcessorSupport;

public class StreamPageable<K, T> implements Pageable<K, T> {
	private final Supplier<Stream<T>> suppler;
	private final K cursorId;
	private final K nextCursorId;

	public StreamPageable(K cursorId, Supplier<Stream<T>> suppler, K nextCursorId) {
		this.cursorId = cursorId;
		this.nextCursorId = nextCursorId;
		this.suppler = suppler;
	}

	@Override
	public List<T> getList() {
		return stream().collect(Collectors.toList());
	}

	@Override
	public K getCursorId() {
		return cursorId;
	}

	@Override
	public K getNextCursorId() {
		return nextCursorId;
	}

	@Override
	public Stream<T> stream() {
		Stream<T> stream = suppler.get();
		if(stream == null) {
			return StreamProcessorSupport.emptyStream();
		}
		return StreamProcessorSupport.autoClose(stream);
	}
}
