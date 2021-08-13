package scw.orm.sql;

import scw.mapper.Field;

public class StandardColumn extends StandardColumnDescriptor implements Column{
	private Field field;
	
	public Field getField() {
		return field;
	}
	public void setField(Field field) {
		this.field = field;
	}
}
