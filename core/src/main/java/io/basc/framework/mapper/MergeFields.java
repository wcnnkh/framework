package io.basc.framework.mapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MergeFields implements Fields {
	private final Fields left;
	private final Fields right;

	public MergeFields(Fields left, Fields right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public Class<?> getCursorId() {
		return left.getCursorId();
	}

	@Override
	public Class<?> getNextCursorId() {
		return right.getNextCursorId();
	}

	@Override
	public boolean hasNext() {
		return right.hasNext();
	}

	@Override
	public List<Field> getList() {
		return Stream.concat(left.getList().stream(), right.getList().stream()).collect(Collectors.toList());
	}

	@Override
	public Stream<Field> stream() {
		return Stream.concat(left.stream(), right.stream());
	}

	@Override
	public Fields jumpTo(Class<?> cursorId) {
		return right.jumpTo(cursorId);
	}
}
