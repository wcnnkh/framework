package scw.orm.sql;

import java.lang.reflect.Field;

import scw.core.reflect.FieldDefinition;
import scw.orm.DefaultFieldDefinitionFactory;

public class TableFieldDefinitionFactory extends DefaultFieldDefinitionFactory {
	@Override
	protected FieldDefinition analysisField(Class<?> clazz, Field field) {
		return new TableFieldDefinitaion(clazz, field);
	}
}
