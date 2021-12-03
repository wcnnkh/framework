package io.basc.framework.mapper;

import io.basc.framework.util.Accept;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AcceptFields implements Fields, Serializable {
	private static final long serialVersionUID = 1L;
	private final Accept<Field> accept;
	private final Fields fields;
	private volatile List<Field> fieldList;

	public AcceptFields(Fields fields, Accept<Field> accept) {
		this.accept = accept;
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
		return fields.stream().filter(accept);
	}

	@Override
	public List<Field> rows() {
		if(fieldList == null) {
			synchronized (this) {
				fieldList = fields.rows().stream().filter(accept).collect(Collectors.toList());
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
		return new AcceptFields(fields, accept);
	}
}
