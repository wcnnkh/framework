package io.basc.framework.sql.orm.support;

import io.basc.framework.mapper.Field;
import io.basc.framework.orm.support.StandardEntityStructure;
import io.basc.framework.sql.orm.Column;
import io.basc.framework.sql.orm.TableMapper;
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

	public static StandardTableStructure init(TableMapper tableMapping, Class<?> entityClass) {
		TableMetadata tableMetadata = tableMapping.resolveMetadata(entityClass);
		StandardTableStructure standardTableStructure = new StandardTableStructure(tableMetadata);
		standardTableStructure.setEntityClass(entityClass);
		return standardTableStructure;
	}

	private static void append(TableMapper tableMapping, StandardTableStructure tableStructure, Class<?> entityClass,
			Field parentField) {
		for (Field field : tableMapping.getFields(entityClass, parentField).all()) {
			if (!field.isSupportGetter()) {
				continue;
			}

			if (tableMapping.isEntity(entityClass, field.getGetter())) {
				append(tableMapping, tableStructure, field.getGetter().getType(), field);
			} else {
				Column column = tableMapping.resolve(entityClass, field);
				StandardColumn standardColumn = new StandardColumn(column);
				if (standardColumn.getField().hasParent()) {
					StringBuilder sb = new StringBuilder();
					for (Field parent : standardColumn.getField().getParents()) {
						sb.append(tableMapping.getName(entityClass, parent.getGetter()));
						sb.append("_");
					}
					sb.append(standardColumn.getName());
					standardColumn.setName(sb.toString());
				}
				tableStructure.getProperties().add(standardColumn);
			}
		}
	}

	public static StandardTableStructure resolveAll(TableMapper tableMapping, Class<?> entityClass) {
		StandardTableStructure standardTableStructure = init(tableMapping, entityClass);
		append(tableMapping, standardTableStructure, entityClass, null);
		return standardTableStructure;
	}
}
