package io.basc.framework.orm;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Fields;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.util.Accept;
import io.basc.framework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StandardProperty extends StandardPropertyDescriptor implements Property {
	private Class<?> entityClass;
	private Field field;

	public StandardProperty() {
	}

	public StandardProperty(Class<?> entityClass, Field field) {
		this(entityClass, OrmUtils.getMapping().getName(entityClass, field.getGetter()), field);
	}

	public StandardProperty(Class<?> entityClass, String name, Field field) {
		this(name, OrmUtils.getMapping().isPrimaryKey(entityClass, field.getGetter()), field);
		this.entityClass = entityClass;
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

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public static List<Property> resolve(Class<?> entityClass, Fields fields, Accept<Field> entityAccept) {
		List<Property> list = new ArrayList<>();
		for (Field field : fields.entity().all()) {
			if (entityAccept.accept(field)) {
				list.addAll(
						resolve(entityClass, MapperUtils.getFields(field.getGetter().getType(), field), entityAccept));
			} else {
				list.add(new StandardProperty(getName(entityClass, field),
						OrmUtils.getMapping().isPrimaryKey(entityClass, field.getGetter()), field));
			}
		}
		return list;
	}

	private static String getName(Class<?> entityClass, Field field) {
		StringBuilder sb = new StringBuilder();
		CollectionUtils.reversal(field.parents().collect(Collectors.toList())).forEach((parent) -> {
			sb.append(OrmUtils.getMapping().getName(entityClass, parent.getGetter()));
			sb.append("_");
		});
		sb.append(OrmUtils.getMapping().getName(entityClass, field.getGetter()));
		return sb.toString();
	}
}
