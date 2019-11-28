package scw.orm.sql;

import java.lang.reflect.Field;

import scw.orm.CacheColumnFactory;
import scw.orm.Column;

public class TableColumnFactory extends CacheColumnFactory {
	@Override
	protected Column analysisField(Class<?> clazz, Field field) {
		return new TableColumn(clazz, field);
	}
}
