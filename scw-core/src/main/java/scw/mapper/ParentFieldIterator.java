package scw.mapper;

import scw.util.AbstractIterator;

public class ParentFieldIterator extends AbstractIterator<Field> {
	private Field field;

	public ParentFieldIterator(Field field) {
		this.field = field;
	}

	@Override
	public boolean hasNext() {
		return field.getParentField() != null;
	}

	@Override
	public Field next() {
		Field field = this.field.getParentField();
		this.field = field;
		return field;
	}
}
