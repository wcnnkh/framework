package io.basc.framework.orm.sql;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Fields;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.StandardProperty;
import io.basc.framework.util.Accept;

import java.util.List;
import java.util.stream.Collectors;

public class StandardColumn extends StandardColumnDescriptor implements Column {
	private Field field;

	public StandardColumn() {
	}

	public StandardColumn(String name, Field field) {
		this(name, false, field);
	}

	public StandardColumn(String name, boolean primaryKey, Field field) {
		super(name, primaryKey);
		this.field = field;
	}

	public StandardColumn(Property property) {
		super(property);
		this.field = property.getField();
	}

	public StandardColumn(StandardColumn standardColumn) {
		super(standardColumn);
		this.field = standardColumn.field;
	}

	public StandardColumn(Column column) {
		super(column);
		this.field = column.getField();
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return MapperUtils.toString(this);
	}

	public static List<Column> resolve(Fields fields, Accept<Field> entityAccept) {
		return StandardProperty.resolve(fields, entityAccept).stream()
				.map((property) -> new StandardColumn(property))
				.collect(Collectors.toList());
	}
}
