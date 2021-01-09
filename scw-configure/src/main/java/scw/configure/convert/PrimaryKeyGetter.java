package scw.configure.convert;

import java.util.Iterator;

import scw.configure.convert.annotation.EntityToMapPrimaryKey;
import scw.convert.TypeDescriptor;
import scw.mapper.Field;
import scw.mapper.FieldFilter;
import scw.mapper.Fields;
import scw.mapper.MapperUtils;

public interface PrimaryKeyGetter {
	
	boolean matches(TypeDescriptor sourceType);

	Object get(Object source, TypeDescriptor sourceType);
	
	static final PrimaryKeyGetter ANNOTATION = new PrimaryKeyGetter() {
		
		private Field getField(TypeDescriptor sourceType){
			Fields fields = MapperUtils.getMapper().getFields(
					sourceType.getType());
			return fields.find(new FieldFilter() {

				public boolean accept(Field field) {
					return field.getGetter().getAnnotatedElement()
							.getAnnotation(EntityToMapPrimaryKey.class) != null;
				}
			});
		}
		
		public boolean matches(TypeDescriptor sourceType) {
			return getField(sourceType) != null;
		};
		
		public Object get(Object source, TypeDescriptor sourceType) {
			return getField(sourceType).getGetter().get(source);
		};
	};

	static final PrimaryKeyGetter FIRST_FIELD = new PrimaryKeyGetter() {
		private Field getField(TypeDescriptor sourceType){
			Iterator<Field> iterator = MapperUtils.getMapper()
					.getFields(sourceType.getType()).iterator();
			if (!iterator.hasNext()) {
				return null;
			}

			return iterator.next();
		}
		
		public boolean matches(TypeDescriptor sourceType) {
			return getField(sourceType) != null;
		};
		
		public Object get(Object source, TypeDescriptor sourceType) {
			return getField(sourceType).getGetter().get(source);
		}
	};

	static final class SpecifyFieldName implements
			PrimaryKeyGetter {
		private final String fieldName;

		public SpecifyFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		
		private Field getField(TypeDescriptor sourceType){
			Fields fields = MapperUtils.getMapper().getFields(
					sourceType.getType());
			Field field = fields.findGetter(fieldName, null);
			if (field == null) {
				return null;
			}
			return field;
		}
		
		public boolean matches(TypeDescriptor sourceType) {
			return getField(sourceType) != null;
		}

		public Object get(Object source, TypeDescriptor sourceType) {
			return getField(sourceType).getGetter().get(source);
		}

	}

	static final class SpecifyField implements PrimaryKeyGetter {
		private final Field field;

		public SpecifyField(Field field) {
			this.field = field;
		}
		
		public boolean matches(TypeDescriptor sourceType) {
			return true;
		}

		public Object get(Object source, TypeDescriptor sourceType) {
			return field.getGetter().get(source);
		}

	}
}
