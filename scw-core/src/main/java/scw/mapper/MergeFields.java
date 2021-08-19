package scw.mapper;

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
	public long getCount() {
		return left.getCount() + right.getCount();
	}

	@Override
	public List<Field> rows() {
		return Stream.concat(left.rows().stream(), right.rows().stream()).collect(Collectors.toList());
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
