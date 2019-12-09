package scw.orm.support;

import java.lang.reflect.Field;

import scw.orm.Column;
import scw.orm.FieldColumn;

public class FieldColumnFactory extends NoCacheColumnFactory {
	@Override
	protected Column analysisField(Class<?> clazz, Field field) {
		return new FieldColumn(clazz, field);
	}
}
