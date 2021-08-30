package io.basc.framework.orm;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Fields;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.util.Accept;
import io.basc.framework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StandardProperty extends StandardPropertyDescriptor implements
		Property {
	private Field field;

	public StandardProperty() {
	}
	
	public StandardProperty(Field field){
		this(OrmUtils.getMapping().getName(field.getGetter()), field);
	}
	
	public StandardProperty(String name, Field field){
		this(name, OrmUtils
				.getMapping().isPrimaryKey(field), field);
	}

	public StandardProperty(String name, boolean primaryKey, Field field) {
		super(name, primaryKey);
		this.field = field;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}
	
	public static List<Property> resolve(Fields fields,
			Accept<Field> entityAccept) {
		List<Property> list = new ArrayList<>();
		for (Field field : fields.entity().all()) {
			if (entityAccept.accept(field)) {
				list.addAll(resolve(MapperUtils.getFields(field.getGetter()
						.getType(), field), entityAccept));
			} else {
				list.add(new StandardProperty(getName(field), OrmUtils
						.getMapping().isPrimaryKey(field), field));
			}
		}
		return list;
	}
	
	private static String getName(Field field) {
		StringBuilder sb = new StringBuilder();
		CollectionUtils.reversal(field.parents().collect(Collectors.toList()))
				.forEach(
						(parent) -> {
							sb.append(OrmUtils.getMapping().getName(
									parent.getGetter()));
							sb.append("_");
						});
		sb.append(OrmUtils.getMapping().getName(field.getGetter()));
		return sb.toString();
	}
}
