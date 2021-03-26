package scw.mapper;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import scw.core.annotation.AnnotatedElementUtils;
import scw.core.annotation.MultiAnnotatedElement;
import scw.core.utils.CollectionUtils;
import scw.core.utils.ObjectUtils;

public class FieldMetadata implements Serializable {
	public static final FieldMetadata[] EMPTY_ARRAY = new FieldMetadata[0];
	private static final long serialVersionUID = 1L;
	private final Getter getter;
	private final Setter setter;
	
	public FieldMetadata(FieldMetadata metadata) {
		this(metadata.getter, metadata.setter);
	}

	public FieldMetadata(Getter getter, Setter setter) {
		this.getter = getter;
		this.setter = setter;
	}

	public Getter getGetter() {
		return getter;
	}

	public Setter getSetter() {
		return setter;
	}

	public boolean isSupportGetter() {
		return getter != null;
	}

	public boolean isSupportSetter() {
		return setter != null;
	}

	/**
	 * 将gettr setter的AnnotatedElement结果合并
	 * 
	 * @return
	 */
	public AnnotatedElement getAnnotatedElement() {
		if (isSupportGetter() && isSupportSetter()) {
			return new MultiAnnotatedElement(getGetter().getAnnotatedElement(), getSetter().getAnnotatedElement());
		}

		if (isSupportGetter()) {
			return getGetter().getAnnotatedElement();
		}

		if (isSupportSetter()) {
			return getSetter().getAnnotatedElement();
		}
		return AnnotatedElementUtils.EMPTY_ANNOTATED_ELEMENT;
	}

	@Override
	public int hashCode() {
		if (getter == null && setter == null) {
			return 0;
		}

		if (getter == null) {
			return setter.hashCode();
		}

		if (setter == null) {
			return getter.hashCode();
		}

		return getter.hashCode() + setter.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof FieldMetadata) {
			return ObjectUtils.nullSafeEquals(getter, ((FieldMetadata) obj).getter)
					&& ObjectUtils.nullSafeEquals(setter, ((FieldMetadata) obj).setter);
		}

		return false;
	}
	
	@Override
	public String toString() {
		if (isSupportGetter() && isSupportSetter()) {
			return "getter {" + getter + "} setter {" + setter + "}";
		}

		if (isSupportGetter()) {
			return "getter {" + getter + "}";
		}

		if (isSupportSetter()) {
			return "setter {" + setter + "}";
		}
		return super.toString();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(Object instance){
		if(!isSupportGetter()){
			return null;
		}
		
		if (instance == null && !Modifier.isStatic(getGetter().getModifiers())) {
			//非静态字段的调用实例不应该为空
			return null;
		}
		return (T) getGetter().get(instance);
	}
	
	public <T> List<T> getValues(Collection<?> instances, boolean nullable){
		if(CollectionUtils.isEmpty(instances)){
			return Collections.emptyList();
		}
		
		if(!isSupportGetter()){
			return Collections.emptyList();
		}
		
		List<T> list = new ArrayList<T>(instances.size());
		for (Object entity : instances) {
			T value = getValue(entity);
			if (value == null && !nullable) {
				continue;
			}

			list.add(value);
		}
		return list;
	}
	
	public <T> List<T> getValues(Collection<?> instances){
		return getValues(instances, false);
	}
}
