package scw.orm.sql;

import java.lang.reflect.Field;

import scw.orm.Column;
import scw.orm.support.CacheColumnFactory;

public class TableColumnFactory extends CacheColumnFactory {
	@Override
	protected Column analysisField(Class<?> clazz, Field field) {
		return new TableColumn(clazz, field);
	}
}
