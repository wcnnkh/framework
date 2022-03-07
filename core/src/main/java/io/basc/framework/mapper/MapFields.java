package io.basc.framework.mapper;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapFields implements Fields, Serializable {
	private static final long serialVersionUID = 1L;
	private final Function<Stream<Field>, Stream<Field>> map;
	private final Fields fields;
	private volatile List<Field> fieldList;

	public MapFields(Fields fields, Function<Stream<Field>, Stream<Field>> map) {
		this.map = map;
		this.fields = fields;
	}

	@Override
	public Class<?> getCursorId() {
		return fields.getCursorId();
	}

	@Override
	public Class<?> getNextCursorId() {
		return fields.getNextCursorId();
	}

	@Override
	public Stream<Field> stream() {
		return map.apply(fields.stream());
	}

	@Override
	public List<Field> getList() {
		if (fieldList == null) {
			synchronized (this) {
				fieldList = map.apply(fields.getList().stream()).collect(Collectors.toList());
			}
		}
		return fieldList;
	}

	@Override
	public boolean hasNext() {
		return fields.hasNext();
	}

	@Override
	public Fields jumpTo(Class<?> cursorId) {
		Fields fields = this.fields.jumpTo(cursorId);
		return new MapFields(fields, map);
	}
}
