package scw.mapper;

import java.io.Serializable;
import java.util.Collection;

public final class EntityMapping implements Serializable, java.lang.Cloneable {
	private static final long serialVersionUID = 1L;
	private final Collection<Column> columns;
	private final EntityMapping superEntityMapping;

	public EntityMapping(Collection<Column> columns, EntityMapping superEntityMapping) {
		this.columns = columns;
		this.superEntityMapping = superEntityMapping;
	}

	public Collection<Column> getColumns() {
		return columns;
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
		private final Field field;
		private final EntityMapping setterEntityMapping;
		private final EntityMapping getterEntityMapping;

		public Column(Field field, EntityMapping getterEntityMapping, EntityMapping setterEntityMapping) {
			this.field = field;
			this.setterEntityMapping = setterEntityMapping;
			this.getterEntityMapping = getterEntityMapping;
		}

		public Field getField() {
			return field;
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
