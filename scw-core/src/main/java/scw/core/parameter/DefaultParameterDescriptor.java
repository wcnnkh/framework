package scw.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import scw.core.annotation.AnnotatedElementUtils;
import scw.core.annotation.AnnotatedElementWrapper;
import scw.core.utils.ObjectUtils;
import scw.lang.Nullable;
import scw.mapper.MapperUtils;

public class DefaultParameterDescriptor extends AnnotatedElementWrapper<AnnotatedElement>
		implements ParameterDescriptor {
	private final String name;
	private final Class<?> type;
	private final Type genericType;

	public DefaultParameterDescriptor(String name, Class<?> type) {
		this(name, type, type);
	}

	public DefaultParameterDescriptor(String name, Class<?> type, @Nullable Type genericType) {
		this(name, AnnotatedElementUtils.EMPTY_ANNOTATED_ELEMENT, type, genericType);
	}

	public DefaultParameterDescriptor(String name, Annotation[] annotations, Class<?> type,
			@Nullable Type genericType) {
		this(name, AnnotatedElementUtils.forAnnotations(annotations), type, genericType);
	}

	public DefaultParameterDescriptor(String name, AnnotatedElement annotatedElement, Class<?> type,
			@Nullable Type genericType) {
		super(annotatedElement);
		this.name = name;
		this.type = type;
		this.genericType = genericType;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public Type getGenericType() {
		return genericType == null ? type : genericType;
	}

	public boolean isNullable() {
		return AnnotatedElementUtils.isNullable(this, () -> false);
	}

	@Override
	public String toString() {
		return MapperUtils.getMapper().getFields(getClass()).getValueMap(this).toString();
	}

	@Override
	public int hashCode() {
		int code = super.hashCode();
		code += name.hashCode();
		code += type.hashCode();
		if (genericType != null) {
			code += genericType.hashCode();
		}
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof DefaultParameterDescriptor) {
			return super.equals(obj) && ObjectUtils.nullSafeEquals(name, ((DefaultParameterDescriptor) obj).name)
					&& ObjectUtils.nullSafeEquals(type, ((DefaultParameterDescriptor) obj).type)
					&& ObjectUtils.nullSafeEquals(genericType, ((DefaultParameterDescriptor) obj).genericType);
		}
		return false;
	}
}
