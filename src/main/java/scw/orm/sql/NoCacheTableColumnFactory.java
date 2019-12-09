package scw.orm.sql;

import java.lang.reflect.Field;

import scw.orm.Column;
import scw.orm.support.NoCacheColumnFactory;

public class NoCacheTableColumnFactory extends NoCacheColumnFactory {
	@Override
	protected Column analysisField(Class<?> clazz, Field field) {
		return new TableColumn(clazz, field);
	}
}
