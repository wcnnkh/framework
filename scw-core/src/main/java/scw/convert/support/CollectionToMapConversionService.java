package scw.convert.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.lang.NotSupportedException;
import scw.mapper.Field;
import scw.mapper.FieldFilter;
import scw.mapper.Fields;
import scw.mapper.Getter;
import scw.mapper.MapperUtils;
import scw.util.CollectionFactory;
import scw.util.XUtils;

public class CollectionToMapConversionService implements ConversionService{
	private static final TypeDescriptor COLLECTION_TYPE = TypeDescriptor.collection(List.class, Object.class);
	
	private final ConversionService conversionService;
	private final PrimaryKeyGetter primaryKeyGetter;
	
	public CollectionToMapConversionService(ConversionService conversionService, PrimaryKeyGetter primaryKeyGetter){
		this.conversionService = conversionService;
		this.primaryKeyGetter = primaryKeyGetter;
	}
	
	public boolean canConvert(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if(!targetType.isMap()){
			return false;
		}
		
		if(sourceType.isCollection() || sourceType.isArray()){
			TypeDescriptor descriptor = sourceType.getElementTypeDescriptor();
			if(descriptor.getType() == Object.class){
				return true;
			}
			return primaryKeyGetter.matches(descriptor);
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if(source == null){
			return null;
		}
		
		Collection sources;
		if(sourceType.isCollection()){
			sources = (Collection)source;
		}else{
			sources = (Collection) conversionService.convert(source, sourceType, COLLECTION_TYPE);
		}

		Map map = CollectionFactory.createMap(targetType.getType(), targetType.getMapKeyTypeDescriptor().getType(), sources.size());
		for(Object item : sources){
			if(item == null){
				continue;
			}
			
			Object value = conversionService.convert(item, sourceType.elementTypeDescriptor(item), targetType.getMapValueTypeDescriptor());
			Object key = primaryKeyGetter.get(value, sourceType.narrow(value));
			key = conversionService.convert(key, sourceType.narrow(key), targetType.getMapKeyTypeDescriptor());
			map.put(key, value);
		}
		return map;
	}
	
	public static interface PrimaryKeyGetter {
		
		boolean matches(TypeDescriptor sourceType);

		Object get(Object source, TypeDescriptor sourceType);
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface CollectionToMapPrimaryKey {
	}
	
	public static final PrimaryKeyGetter ANNOTATION = new PrimaryKeyGetter() {
		
		private Getter getGetter(TypeDescriptor sourceType){
			Fields fields = MapperUtils.getMapper().getFields(
					sourceType.getType());
			Field field = fields.find(new FieldFilter() {

				public boolean accept(Field field) {
					return field.getGetter().getAnnotatedElement()
							.getAnnotation(CollectionToMapPrimaryKey.class) != null;
				}
			});
			return field == null? null:field.getGetter();
		}
		
		public boolean matches(TypeDescriptor sourceType) {
			return getGetter(sourceType) != null;
		};
		
		public Object get(Object source, TypeDescriptor sourceType) {
			return getGetter(sourceType).get(source);
		};
	};

	public static final PrimaryKeyGetter FIRST_FIELD = new PrimaryKeyGetter() {
		private Getter getGetter(TypeDescriptor sourceType){
			Iterator<Field> iterator = MapperUtils.getMapper()
					.getFields(sourceType.getType()).iterator();
			if (!iterator.hasNext()) {
				return null;
			}

			return iterator.next().getGetter();
		}
		
		public boolean matches(TypeDescriptor sourceType) {
			return getGetter(sourceType) != null;
		};
		
		public Object get(Object source, TypeDescriptor sourceType) {
			return getGetter(sourceType).get(source);
		}
	};

	public static final class SpecifyFieldName implements
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

	public static final class SpecifyField implements PrimaryKeyGetter {
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
	
	public static class ConfigurablePrimaryKeyGetter implements PrimaryKeyGetter, Comparator<PrimaryKeyGetter>{
		protected final TreeSet<PrimaryKeyGetter> primaryKeyGetters = new TreeSet<PrimaryKeyGetter>(this);
		
		public SortedSet<PrimaryKeyGetter> getPrimaryKeyGetters(){
			return XUtils.synchronizedProxy(primaryKeyGetters, this);
		}
		
		public PrimaryKeyGetter getPrimaryKeyGetter(TypeDescriptor sourceType){
			for(PrimaryKeyGetter primaryKeyGetter : primaryKeyGetters){
				if(primaryKeyGetter.matches(sourceType)){
					return primaryKeyGetter;
				}
			}
			return null;
		}
		
		public boolean matches(TypeDescriptor sourceType) {
			return getPrimaryKeyGetter(sourceType) != null;
		}

		public Object get(Object source, TypeDescriptor sourceType) {
			PrimaryKeyGetter primaryKeyGetter = getPrimaryKeyGetter(sourceType);
			if(primaryKeyGetter == null){
				throw new NotSupportedException(sourceType.getType().getName());
			}
			
			return primaryKeyGetter.get(source, sourceType);
		}

		public int compare(PrimaryKeyGetter o1, PrimaryKeyGetter o2) {
			return -1;
		}
	}
}
