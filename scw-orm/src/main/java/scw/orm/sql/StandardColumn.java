package scw.orm.sql;

import java.util.List;
import java.util.stream.Collectors;

import scw.mapper.Field;
import scw.mapper.Fields;
import scw.mapper.MapperUtils;
import scw.orm.Property;
import scw.orm.StandardProperty;
import scw.util.Accept;

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
