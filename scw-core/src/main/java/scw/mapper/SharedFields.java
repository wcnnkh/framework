package scw.mapper;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import scw.lang.NotSupportedException;
import scw.util.page.Pageable;

public class SharedFields implements Fields, Serializable {
	private static final long serialVersionUID = 1L;
	private final Class<?> cursorId;
	private final List<Field> fields;

	public SharedFields(Class<?> cursorId, List<Field> fields) {
		this.cursorId = cursorId;
		this.fields = fields;
	}

	@Override
	public Fields shared() {
		return new SharedFields(cursorId, fields);
	}
	
	@Override
	public long getCount() {
		return fields.size();
	}

	@Override
	public Class<?> getCursorId() {
		return cursorId;
	}

	@Override
	public Class<?> getNextCursorId() {
		return null;
	}

	@Override
	public List<Field> rows() {
		return Collections.unmodifiableList(fields);
	}

	@Override
	public boolean hasNext() {
		return false;
	}
	
	@Override
	public Fields all() {
		return this;
	}

	@Override
	public Pageable<Class<?>, Field> process(Class<?> start, long count) {
		if (start == cursorId) {
			return this;
		}
		throw new NotSupportedException("Shared");
	}

	@Override
	public Stream<Field> stream() {
		return fields.stream();
	}
}
