package io.basc.framework.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.annotation.AnnotatedElementWrapper;
import io.basc.framework.core.annotation.AnnotatedElements;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ObjectUtils;

public class DefaultParameterDescriptor extends AnnotatedElementWrapper<AnnotatedElement>
		implements ParameterDescriptor {
	private final String name;
	private final Class<?> type;
	private final Type genericType;

	public DefaultParameterDescriptor(String name, Class<?> type) {
		this(name, type, type);
	}

	public DefaultParameterDescriptor(String name, Class<?> type, @Nullable Type genericType) {
		this(name, AnnotatedElements.EMPTY, type, genericType);
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

	@Override
	public String toString() {
		Map<String, Object> map = new HashMap<String, Object>(4, 1);
		map.put("name", name);
		map.put("type", type);
		map.put("genericType", genericType);
		return map.toString();
	}

	@Override
	public int hashCode() {
		int code = super.hashCode();
		if (name != null) {
			code += name.hashCode();
		}
		if (type != null) {
			code += type.hashCode();
		}

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
			return super.equals(obj) && ObjectUtils.equals(name, ((DefaultParameterDescriptor) obj).name)
					&& ObjectUtils.equals(type, ((DefaultParameterDescriptor) obj).type)
					&& ObjectUtils.equals(genericType, ((DefaultParameterDescriptor) obj).genericType);
		}
		return false;
	}
}
