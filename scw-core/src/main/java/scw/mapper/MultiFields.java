package scw.mapper;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import scw.util.MultiIterable;

public class MultiFields implements Fields, Serializable {
	private static final long serialVersionUID = 1L;
	private Collection<Fields> mergeFields;

	public MultiFields(Fields... mergeFields) {
		this(Arrays.asList(mergeFields));
	}

	public MultiFields(Collection<Fields> mergeFields) {
		this.mergeFields = mergeFields;
	}

	@Override
	public Iterator<Field> iterator() {
		return new MultiIterable<Field>(mergeFields).iterator();
	}

	@Override
	public int size() {
		int size = 0;
		for (Fields fields : mergeFields) {
			size += fields.size();
		}
		return size;
	}
}
