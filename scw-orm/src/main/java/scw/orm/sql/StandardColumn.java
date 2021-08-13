package scw.orm.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import scw.core.utils.CollectionUtils;
import scw.mapper.Field;
import scw.mapper.FieldFeature;
import scw.mapper.Fields;
import scw.mapper.MapperUtils;
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
	
	@Override
	public String toString() {
		return MapperUtils.toString(this);
	}

	public static List<Column> wrapper(Fields fields, Accept<Field> entityAccept) {
		List<Column> list = new ArrayList<>();
		for (Field field : fields.entity().all().strict().accept(FieldFeature.EXISTING_FIELD)) {
			if (entityAccept.accept(field)) {
				list.addAll(wrapper(OrmUtils.getMapping().getFields(field.getGetter().getType(), field).all(),
						entityAccept));
			} else {
				list.add(new StandardColumn(getName(field), OrmUtils.getMapping().isPrimaryKey(field),
						field));
			}
		}
		return list;
	}
	
	private static String getName(Field field){
		StringBuilder sb = new StringBuilder();
		CollectionUtils.reversal(field.parents().collect(Collectors.toList())).forEach((parent) -> {
			sb.append(OrmUtils.getMapping().getName(parent.getGetter()));
			sb.append("_");
		});
		sb.append(OrmUtils.getMapping().getName(field.getGetter()));
		return sb.toString();
	}
}
