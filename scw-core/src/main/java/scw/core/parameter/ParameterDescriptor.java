package scw.core.parameter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import scw.lang.DefaultValue;
import scw.lang.Nullable;
import scw.util.Named;
import scw.value.StringValue;
import scw.value.Value;

public interface ParameterDescriptor extends AnnotatedElement, Named{
	public static final ParameterDescriptor[] EMPTY_ARRAY = new ParameterDescriptor[0];
	
	String getName();

	Class<?> getType();

	Type getGenericType();
	
	/**
	 * 是否可以为空
	 * @return
	 */
	boolean isNullable();
	
	@Nullable
	default Value getDefaultValue() {
		DefaultValue defaultValue = getAnnotation(DefaultValue.class);
		if(defaultValue == null){
			return null;
		}
		return new StringValue(defaultValue.value());
	}
}
