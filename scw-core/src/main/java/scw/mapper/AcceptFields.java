package scw.mapper;

import java.io.Serializable;
import java.util.Iterator;

import scw.util.Accept;
import scw.util.AcceptIterator;

public class AcceptFields implements Fields, Serializable {
	private static final long serialVersionUID = 1L;
	private final Accept<Field> accept;
	private final Fields fields;

	public AcceptFields(Fields fields, Accept<Field> accept) {
		this.accept = accept;
		this.fields = fields;
	}

	public Iterator<Field> iterator() {
		if (accept == null) {
			return fields.iterator();
		}
		return new AcceptIterator<Field>(fields.iterator(), accept);
	}
}
