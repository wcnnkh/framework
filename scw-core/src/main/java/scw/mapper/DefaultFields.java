package scw.mapper;

import java.util.Iterator;
import java.util.NoSuchElementException;

import scw.util.AbstractIterator;

public class DefaultFields implements Fields {
	private final FieldMetadataFactory metadataFactory;
	private final Class<?> entityClass;
	private final boolean useSuperClass;
	private final Field parentField;

	public DefaultFields(FieldMetadataFactory metadataFactory, Class<?> entityClass, boolean useSuperClass, Field parentField) {
		this.metadataFactory = metadataFactory;
		this.entityClass = entityClass;
		this.useSuperClass = useSuperClass;
		this.parentField = parentField;
	}

	public Iterator<Field> iterator() {
		return new FieldIterator();
	}

	protected Field createField(FieldMetadata fieldMetadata) {
		return new Field(parentField, fieldMetadata);
	}

	private final class FieldIterator extends AbstractIterator<Field> {
		private Iterator<FieldMetadata> iterator;
		private Class<?> entityClass = DefaultFields.this.entityClass;

		public FieldIterator() {
			this.iterator = metadataFactory.getFieldMetadatas(entityClass).iterator();
		}

		public boolean hasNext() {
			if(iterator.hasNext()){
				return true;
			}

			if (useSuperClass && entityClass !=null && entityClass != Object.class) {
				this.entityClass = entityClass.getSuperclass();
				if (entityClass == null || entityClass == Object.class) {
					return false;
				}

				iterator = metadataFactory.getFieldMetadatas(entityClass).iterator();
				return hasNext();
			}
			return false;
		}

		public Field next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			
			return createField(iterator.next());
		}
	}
}