package scw.mapper;

import java.util.Iterator;
import java.util.NoSuchElementException;

import scw.util.AbstractIterator;

public abstract class IterableFields extends AbstractFields {
	private final Class<?> entityClass;
	private final boolean useSuperClass;
	private final FieldFilter fieldFilter;
	private final Field parentField;

	public IterableFields(Class<?> entityClass, boolean useSuperClass, FieldFilter fieldFilter, Field parentField) {
		this.entityClass = entityClass;
		this.useSuperClass = useSuperClass;
		this.fieldFilter = fieldFilter;
		this.parentField = parentField;
	}

	public Iterator<Field> iterator() {
		return new FieldIterator();
	}

	protected abstract Iterator<FieldMetadata> getFieldMetadataIterator(Class<?> entityClass);

	protected Field createField(FieldMetadata fieldMetadata) {
		return new Field(parentField, fieldMetadata.getGetter(), fieldMetadata.getSetter());
	}

	private final class FieldIterator extends AbstractIterator<Field> {
		private Iterator<FieldMetadata> iterator;
		private Field currentField;
		private Class<?> entityClass = IterableFields.this.entityClass;

		public FieldIterator() {
			this.iterator = getFieldMetadataIterator(entityClass);
		}

		public boolean hasNext() {
			if (currentField != null) {
				return true;
			}

			while (iterator.hasNext()) {
				FieldMetadata metadata = iterator.next();
				Field field = createField(metadata);
				if (fieldFilter == null || fieldFilter.accept(field)) {
					this.currentField = field;
					return true;
				}
			}

			if (useSuperClass && entityClass !=null && entityClass != Object.class) {
				this.entityClass = entityClass.getSuperclass();
				if (entityClass == null || entityClass == Object.class) {
					return false;
				}

				iterator = getFieldMetadataIterator(entityClass);
				return hasNext();
			}
			return false;
		}

		public Field next() {
			if (currentField == null && !hasNext()) {
				throw new NoSuchElementException();
			}

			try {
				return currentField.clone();
			} finally {
				this.currentField = null;
			}
		}
	}
}
