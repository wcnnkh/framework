package scw.mapper;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import scw.util.Accept;
import scw.util.page.Pageable;

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
	public long getCount() {
		return rows().size();
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
	public Pageable<Class<?>, Field> process(Class<?> start, long count) {
		return fields.process(start, count);
	}
}
