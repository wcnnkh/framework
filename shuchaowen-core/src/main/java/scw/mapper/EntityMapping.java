package scw.mapper;

import java.io.Serializable;

public class EntityMapping implements Serializable{
	private static final long serialVersionUID = 1L;
	private final Field[] fields;
	private final Class<?> entityClass;
	private final EntityMapping superEntityMapping;

	public EntityMapping(Class<?> entityClass, Field[] fields, EntityMapping superEntityMapping) {
		this.entityClass = entityClass;
		this.fields = fields;
		this.superEntityMapping =superEntityMapping;
	}

	public Field[] getFields() {
		return fields.clone();
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public EntityMapping getSuperEntityMapping() {
		return superEntityMapping;
	}
}
