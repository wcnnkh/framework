package scw.orm.sql.annotation;

import java.lang.reflect.Field;

import scw.orm.annotation.FieldProperty;

public class FieldColumn extends FieldProperty{
	private static final long serialVersionUID = 1L;

	public FieldColumn(Field field){
		super(field);
	}
}
