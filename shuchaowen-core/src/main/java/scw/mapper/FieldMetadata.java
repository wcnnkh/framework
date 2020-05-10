package scw.mapper;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;

import scw.core.annotation.AnnotatedElementUtils;
import scw.core.annotation.MultiAnnotatedElement;

public class FieldMetadata implements Serializable {
	public static final FieldMetadata[] EMPTY_ARRAY = new FieldMetadata[0];
	private static final long serialVersionUID = 1L;
	private final Getter getter;
	private final Setter setter;

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
	 * 将gett setter的AnnotatedElement结果合并
	 * @return
	 */
	public AnnotatedElement getAnnotatedElement(){
		if(isSupportGetter() && isSupportSetter()){
			return new MultiAnnotatedElement(getGetter().getAnnotatedElement(), getSetter().getAnnotatedElement());
		}
		
		if(isSupportGetter()){
			return getGetter().getAnnotatedElement();
		}
		
		if(isSupportSetter()){
			return getSetter().getAnnotatedElement();
		}
		return AnnotatedElementUtils.EMPTY_ANNOTATED_ELEMENT;
	}
}
