package scw.orm.sql.support;

import java.lang.reflect.Field;

import scw.orm.Column;
import scw.orm.support.FieldColumnFactory;

public class TableColumnFactory extends FieldColumnFactory {
	@Override
	protected Column analysisField(Class<?> clazz, Field field) {
		return new TableFieldColumn(clazz, field);
	}
}
