package scw.mapper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scw.core.utils.CollectionUtils;
import scw.core.utils.ObjectUtils;
import scw.util.Accept;
import scw.util.AcceptIterator;

public abstract class AbstractFields implements Fields {

	private boolean acceptFieldDescriptor(FieldDescriptor descriptor, String name, Type type) {
		if (type != null) {
			if (type instanceof Class) {
				if (!type.equals(descriptor.getType())) {
					return false;
				}
			} else {
				if (!type.equals(descriptor.getGenericType())) {
					return false;
				}
			}
		}
		return name.equals(descriptor.getName());
	}

	public Field findGetter(final String name, final Type type) {
		return accept(new Accept<Field>() {

			public boolean accept(Field field) {
				return field.isSupportGetter() && acceptFieldDescriptor(field.getGetter(), name, type);
			}
		}).first();
	}

	public Field findSetter(final String name, final Type type) {
		return accept(new Accept<Field>() {

			public boolean accept(Field field) {
				return field.isSupportSetter() && acceptFieldDescriptor(field.getSetter(), name, type);
			}
		}).first();
	}

	public Field find(final String name, final Type type) {
		return accept(new Accept<Field>() {

			public boolean accept(Field field) {
				return (field.isSupportGetter() && acceptFieldDescriptor(field.getGetter(), name, type))
						|| (field.isSupportSetter() && acceptFieldDescriptor(field.getSetter(), name, type));
			}
		}).first();
	}
	
	public Fields accept(Accept<Field> accept) {
		return new AcceptFields(accept, false);
	}
	
	public Fields accept(FieldFeature... features) {
		return new FeatureFields(features, false);
	}
	
	public Fields exclude(Accept<Field> accept) {
		return new AcceptFields(accept, true);
	}
	
	public Fields exclude(FieldFeature... features) {
		return new FeatureFields(features, true);
	}
	
	public Fields exclude(final Collection<String> names) {
		if(CollectionUtils.isEmpty(names)){
			return this;
		}
		
		return exclude(new Accept<Field>(){

			public boolean accept(Field e) {
				return (e.isSupportGetter() && names.contains(e.getGetter().getName())) || (e.isSupportSetter() && names.contains(e.getSetter().getName()));
			}
		});
	}
	
	public Fields duplicateRemoval() {
		Set<Field> fields = new LinkedHashSet<Field>();
		for(Field field : this){
			fields.add(field);
		}
		return new SharedFields(fields);
	}
	
	public Fields shared() {
		List<Field> fields = new ArrayList<Field>();
		for(Field field : this){
			fields.add(field);
		}
		return new SharedFields(fields);
	}
	
	public Field first() {
		for (Field field : this) {
			return field;
		}
		return null;
	}
	
	/**
	 * 默认情况下是使用迭代的方式处理的，请重写此方法尽量优化
	 */
	@SuppressWarnings("unused")
	public int size() {
		int size = 0;
		for(Field field : this){
			size ++;
		}
		return size;
	}
	
	public Map<String, Object> getValueMap(Object instance, boolean nullable) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (Field field : this) {
			if(!field.isSupportGetter()){
				continue;
			}
			
			String name = field.getGetter().getName();
			if (map.containsKey(name)) {
				continue;
			}
			
			Object value = field.getValue(instance);
			if (value == null && !nullable) {
				continue;
			}
			map.put(name, value);
		}
		return map;
	}
	
	public Map<String, Object> getValueMap(Object instance) {
		return getValueMap(instance, false);
	}
	
	public void test(Object instance, FieldTest test)
			throws IllegalArgumentException {
		for (scw.mapper.Field field : this) {
			if (!field.isSupportGetter() || field.getGetter().getField() == null) {
				continue;
			}
			
			if(field.getSetter().isNullable()){
				continue;
			}

			Object value = field.getGetter().get(instance);
			if (ObjectUtils.isEmpty(value)) {
				throw new IllegalArgumentException(field.getGetter().toString());
			}

			if (test == null || test.test(field, value)) {
				continue;
			}

			throw new IllegalArgumentException(field.getGetter().toString());
		}
	}
	
	private final class FeatureFields extends AbstractFields implements Accept<Field>{
		private final FieldFeature[] features;
		private final boolean exclude;
		
		public FeatureFields(FieldFeature[] features, boolean exclude){
			this.features = features;
			this.exclude = exclude;
		}
		
		public Iterator<Field> iterator() {
			if(features == null || features.length == 0){
				return AbstractFields.this.iterator();
			}
			
			return new AcceptIterator<Field>(AbstractFields.this.iterator(), FeatureFields.this);
		}

		public boolean accept(Field e) {
			for(FieldFeature feature : features){
				if(feature == null || feature.getAccept().accept(e)){
					if(exclude){
						return false;
					}
					continue;
				}
				
				if(exclude){
					return true;
				}
				return false;
			}
			return true;
		}
	}
	
	private final class AcceptFields extends AbstractFields implements Accept<Field>{
		private final Accept<Field> accept;
		private final boolean exclude;
		
		public AcceptFields(Accept<Field> accept, boolean exclude){
			this.accept = accept;
			this.exclude = exclude;
		}
		
		public Iterator<Field> iterator() {
			if(accept == null){
				return AbstractFields.this.iterator();
			}
			
			return new AcceptIterator<Field>(AbstractFields.this.iterator(), this);
		}

		public boolean accept(Field e) {
			if(exclude){
				return !accept.accept(e);
			}else{
				return accept.accept(e);
			}
		}
	}
}
