package scw.mapper;

import java.io.Serializable;

public final class EntityMapping implements Serializable, java.lang.Cloneable {
	private static final long serialVersionUID = 1L;
	private final Column[] columns;
	private final Class<?> entityClass;
	private final EntityMapping superEntityMapping;

	public EntityMapping(Class<?> entityClass, Column[] columns, EntityMapping superEntityMapping) {
		this.entityClass = entityClass;
		this.columns = columns;
		this.superEntityMapping = superEntityMapping;
	}

	public Column[] getColumns() {
		return columns;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public EntityMapping getSuperEntityMapping() {
		return superEntityMapping;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public static final class Column implements Serializable, java.lang.Cloneable {
		private static final long serialVersionUID = 1L;
		private final FieldContext fieldContext;
		private final EntityMapping setterEntityMapping;
		private final EntityMapping getterEntityMapping;

		public Column(FieldContext fieldContext, EntityMapping getterEntityMapping, EntityMapping setterEntityMapping) {
			this.fieldContext = fieldContext;
			this.setterEntityMapping = setterEntityMapping;
			this.getterEntityMapping = getterEntityMapping;
		}

		public FieldContext getFieldContext() {
			return fieldContext;
		}

		public EntityMapping getSetterEntityMapping() {
			return setterEntityMapping;
		}

		public EntityMapping getGetterEntityMapping() {
			return getterEntityMapping;
		}

		public boolean isEntity() {
			return setterEntityMapping != null || getterEntityMapping != null;
		}
		
		@Override
		protected Object clone() throws CloneNotSupportedException {
			return super.clone();
		}
	}
}
