package scw.core.parameter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import scw.lang.Nullable;
import scw.util.Named;
import scw.value.Value;

public interface ParameterDescriptor extends Named{
	public static final ParameterDescriptor[] EMPTY_ARRAY = new ParameterDescriptor[0];
	
	AnnotatedElement getAnnotatedElement();
	
	String getName();

	Class<?> getType();

	Type getGenericType();
	
	/**
	 * 是否可以为空
	 * @return
	 */
	boolean isNullable();
	
	@Nullable
	Value getDefaultValue();
}
