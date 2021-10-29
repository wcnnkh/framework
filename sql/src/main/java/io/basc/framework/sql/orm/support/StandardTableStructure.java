package io.basc.framework.sql.orm.support;

import io.basc.framework.mapper.Field;
import io.basc.framework.orm.support.StandardEntityStructure;
import io.basc.framework.sql.orm.Column;
import io.basc.framework.sql.orm.TableMapping;
import io.basc.framework.sql.orm.TableMetadata;
import io.basc.framework.sql.orm.TableStructure;

public class StandardTableStructure extends StandardEntityStructure<Column> implements TableStructure {
	private String engine;
	private String rowFormat;

	public StandardTableStructure(TableMetadata tableMetadata) {
		super(tableMetadata);
		this.engine = tableMetadata.getEngine();
		this.rowFormat = tableMetadata.getRowFormat();
	}

	@Override
	public String getEngine() {
		return engine;
	}

	@Override
	public String getRowFormat() {
		return rowFormat;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public void setRowFormat(String rowFormat) {
		this.rowFormat = rowFormat;
	}

	public static StandardTableStructure init(TableMapping tableMapping, Class<?> entityClass) {
		TableMetadata tableMetadata = tableMapping.resolveMetadata(entityClass);
		return new StandardTableStructure(tableMetadata);
	}

	private static void append(TableMapping tableMapping, StandardTableStructure tableStructure, Class<?> entityClass,
			Field parentField) {
		for (Field field : tableMapping.getFields(entityClass, parentField)) {
			if (!field.isSupportGetter()) {
				continue;
			}

			if (tableMapping.isEntity(entityClass, field.getGetter())) {
				append(tableMapping, tableStructure, field.getGetter().getType(), field);
			} else {
				tableStructure.getProperties().add(tableMapping.resolve(entityClass, field));
			}
		}
	}

	public static StandardTableStructure resolveAll(TableMapping tableMapping, Class<?> entityClass) {
		StandardTableStructure standardTableStructure = init(tableMapping, entityClass);
		append(tableMapping, standardTableStructure, entityClass, null);
		return standardTableStructure;
	}
}
