package run.soeasy.framework.core.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.annotation.MergedAnnotatedElement;
import run.soeasy.framework.core.type.ClassUtils;
import run.soeasy.framework.core.type.ResolvableType;

@Getter
public class TypeDescriptor extends MergedAnnotatedElement {
	private static final Map<Class<?>, TypeDescriptor> commonTypesCache = new HashMap<>(32);

	private static final Class<?>[] CACHED_COMMON_TYPES = { boolean.class, Boolean.class, byte.class, Byte.class,
			char.class, Character.class, double.class, Double.class, float.class, Float.class, int.class, Integer.class,
			long.class, Long.class, short.class, Short.class, String.class, Object.class };

	static {
		for (Class<?> preCachedClass : CACHED_COMMON_TYPES) {
			commonTypesCache.put(preCachedClass, valueOf(preCachedClass));
		}
	}

	private final Class<?> type;

	@NonNull
	private final ResolvableType resolvableType;

	public TypeDescriptor(@NonNull ResolvableType resolvableType, Class<?> type,
			@NonNull AnnotatedElement... annotatedElements) {
		super(annotatedElements.length == 0 ? Collections.emptyList() : Arrays.asList(annotatedElements));
		this.resolvableType = resolvableType;
		this.type = (type != null ? type : resolvableType.getRawType());
	}

	public Class<?> getObjectType() {
		return ClassUtils.resolvePrimitiveIfNecessary(getType());
	}

	public TypeDescriptor narrow(Object value) {
		if (value == null) {
			return this;
		}
		ResolvableType narrowed = ResolvableType.forType(value.getClass(), getResolvableType());
		return new TypeDescriptor(narrowed, value.getClass(), this);
	}

	public TypeDescriptor upcast(Class<?> superType) {
		if (superType == null) {
			return null;
		}
		Assert.isAssignable(superType, getType());
		return new TypeDescriptor(getResolvableType().as(superType), superType, this);
	}

	public String getName() {
		return ClassUtils.getQualifiedName(getType());
	}

	public boolean isPrimitive() {
		return getType().isPrimitive();
	}

	public boolean isAssignableTo(TypeDescriptor typeDescriptor) {
		boolean typesAssignable = typeDescriptor.getObjectType().isAssignableFrom(getObjectType());
		if (!typesAssignable) {
			return false;
		}
		if (isArray() && typeDescriptor.isArray()) {
			return isNestedAssignable(getElementTypeDescriptor(), typeDescriptor.getElementTypeDescriptor());
		} else if (isCollection() && typeDescriptor.isCollection()) {
			return isNestedAssignable(getElementTypeDescriptor(), typeDescriptor.getElementTypeDescriptor());
		} else if (isMap() && typeDescriptor.isMap()) {
			return isNestedAssignable(getMapKeyTypeDescriptor(), typeDescriptor.getMapKeyTypeDescriptor())
					&& isNestedAssignable(getMapValueTypeDescriptor(), typeDescriptor.getMapValueTypeDescriptor());
		} else {
			return true;
		}
	}

	private boolean isNestedAssignable(TypeDescriptor nestedTypeDescriptor, TypeDescriptor otherNestedTypeDescriptor) {

		return (nestedTypeDescriptor == null || otherNestedTypeDescriptor == null
				|| nestedTypeDescriptor.isAssignableTo(otherNestedTypeDescriptor));
	}

	public boolean isCollection() {
		return Collection.class.isAssignableFrom(getType());
	}

	public boolean isArray() {
		return getType().isArray();
	}

	public TypeDescriptor map(@NonNull Function<? super ResolvableType, ? extends ResolvableType> mapper) {
		return new TypeDescriptor(mapper.apply(getResolvableType()), null, this);
	}

	public TypeDescriptor getElementTypeDescriptor() {
		if (getResolvableType().isArray()) {
			return new TypeDescriptor(getResolvableType().getComponentType(), null, this);
		}
		return upcast(Collection.class).map((e) -> e.getActualTypeArgument(0));
	}

	public TypeDescriptor elementTypeDescriptor(Object element) {
		return narrow(element, getElementTypeDescriptor());
	}

	public boolean isMap() {
		return Map.class.isAssignableFrom(getType());
	}

	public TypeDescriptor getMapKeyTypeDescriptor() {
		Assert.state(isMap(), "Not a [java.util.Map]");
		return upcast(Map.class).map((e) -> e.getActualTypeArgument(0));
	}

	public TypeDescriptor getMapKeyTypeDescriptor(Object mapKey) {
		return narrow(mapKey, getMapKeyTypeDescriptor());
	}

	public TypeDescriptor getMapValueTypeDescriptor() {
		Assert.state(isMap(), "Not a [java.util.Map]");
		return upcast(Map.class).map((e) -> e.getActualTypeArgument(1));
	}

	public TypeDescriptor getMapValueTypeDescriptor(Object mapValue) {
		return narrow(mapValue, getMapValueTypeDescriptor());
	}

	private TypeDescriptor narrow(Object value, TypeDescriptor typeDescriptor) {
		if (typeDescriptor != null) {
			return typeDescriptor.narrow(value);
		}
		if (value != null) {
			return narrow(value);
		}
		return null;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TypeDescriptor)) {
			return false;
		}
		TypeDescriptor otherDesc = (TypeDescriptor) other;
		if (getType() != otherDesc.getType()) {
			return false;
		}
		if (!annotationsMatch(otherDesc)) {
			return false;
		}
		if (isCollection() || isArray()) {
			return ObjectUtils.equals(getElementTypeDescriptor(), otherDesc.getElementTypeDescriptor());
		} else if (isMap()) {
			return (ObjectUtils.equals(getMapKeyTypeDescriptor(), otherDesc.getMapKeyTypeDescriptor())
					&& ObjectUtils.equals(getMapValueTypeDescriptor(), otherDesc.getMapValueTypeDescriptor()));
		} else {
			return true;
		}
	}

	private boolean annotationsMatch(TypeDescriptor otherDesc) {
		Annotation[] anns = getAnnotations();
		Annotation[] otherAnns = otherDesc.getAnnotations();
		if (anns == otherAnns) {
			return true;
		}
		if (anns.length != otherAnns.length) {
			return false;
		}
		if (anns.length > 0) {
			for (int i = 0; i < anns.length; i++) {
				if (!annotationEquals(anns[i], otherAnns[i])) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean annotationEquals(Annotation ann, Annotation otherAnn) {
		// Annotation.equals is reflective and pretty slow, so let's check identity and
		// proxy type first.
		return (ann == otherAnn || (ann.getClass() == otherAnn.getClass() && ann.equals(otherAnn)));
	}

	@Override
	public int hashCode() {
		return getType().hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Annotation ann : getAnnotations()) {
			builder.append('@').append(ann.annotationType().getName()).append(' ');
		}
		builder.append(getResolvableType());
		return builder.toString();
	}

	public static TypeDescriptor forObject(Object source) {
		return (source != null ? valueOf(source.getClass()) : null);
	}

	public static TypeDescriptor valueOf(Class<?> type) {
		if (type == null) {
			type = Object.class;
		}
		TypeDescriptor desc = commonTypesCache.get(type);
		return (desc != null ? desc : new TypeDescriptor(ResolvableType.forType(type), null));
	}

	public static <T> TypeDescriptor forClassWithGenerics(Class<T> type, Type... generics) {
		return new TypeDescriptor(ResolvableType.forClassWithGenerics(type, generics), null);
	}

	public static TypeDescriptor collection(@NonNull Class<?> collectionType, Type elementType) {
		if (!Collection.class.isAssignableFrom(collectionType)) {
			throw new IllegalArgumentException("Collection type must be a [java.util.Collection]");
		}
		return forClassWithGenerics(collectionType, elementType);
	}

	public static TypeDescriptor map(@NonNull Class<?> mapType, @NonNull Type keyType, @NonNull Type valueType) {
		if (!Map.class.isAssignableFrom(mapType)) {
			throw new IllegalArgumentException("Map type must be a [java.util.Map]");
		}
		return forClassWithGenerics(mapType, keyType, valueType);
	}

	public static TypeDescriptor array(TypeDescriptor elementTypeDescriptor) {
		if (elementTypeDescriptor == null) {
			return null;
		}
		return new TypeDescriptor(ResolvableType.forArrayComponent(elementTypeDescriptor.resolvableType), null,
				elementTypeDescriptor);
	}

	public static TypeDescriptor forMethodReturnType(@NonNull Executable executable) {
		if (executable instanceof Method) {
			return new TypeDescriptor(ResolvableType.forType(((Method) executable).getGenericReturnType()), null,
					executable);
		}
		return new TypeDescriptor(ResolvableType.forType(executable.getDeclaringClass()), null, executable);
	}

	public static TypeDescriptor forFieldType(@NonNull Field field) {
		return new TypeDescriptor(ResolvableType.forType(field.getGenericType()), null, field);
	}

	public static TypeDescriptor forParameter(@NonNull Parameter parameter) {
		return new TypeDescriptor(ResolvableType.forType(parameter.getParameterizedType()), null, parameter);
	}

	public static TypeDescriptor forExecutableParameter(Executable executable, int index) {
		if (index >= executable.getParameterCount()) {
			throw new IndexOutOfBoundsException("index: " + index);
		}
		Parameter parameter = executable.getParameters()[index];
		return forParameter(parameter);
	}
}
