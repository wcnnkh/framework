package scw.orm.sql;

import java.util.ArrayList;
import java.util.List;

import scw.mapper.Field;
import scw.mapper.FieldFeature;
import scw.mapper.Fields;
import scw.orm.OrmUtils;
import scw.util.Accept;

public class StandardColumn extends StandardColumnDescriptor implements Column {
	private Field field;

	public StandardColumn() {
	}

	public StandardColumn(String name, Field field) {
		setName(name);
		setField(field);
	}

	public StandardColumn(String name, boolean primaryKey, Field field) {
		setName(name);
		setField(field);
		setPrimaryKey(primaryKey);
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public static List<StandardColumn> wrapper(Fields fields, Accept<Field> entityAccept) {
		List<StandardColumn> list = new ArrayList<>();
		for (Field field : fields.entity().strict().accept(FieldFeature.EXISTING_FIELD)) {
			if (entityAccept.accept(field)) {
				list.addAll(wrapper(OrmUtils.getMapping().getFields(field.getGetter().getType(), field).all(),
						entityAccept));
			} else {
				list.add(new StandardColumn(field.getGetter().getName(), OrmUtils.getMapping().isPrimaryKey(field),
						field));
			}
		}
		return list;
	}
}
