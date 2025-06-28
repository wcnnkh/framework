package run.soeasy.framework.core.type;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.StringUtils;

@FunctionalInterface
public interface ResolvableType extends ParameterizedType, WildcardType, TypeVariableResolver {
	public static final ResolvableType[] EMPTY_TYPES_ARRAY = new ResolvableType[0];
	public static final ResolvableType NONE = new GenericType<>(Object.class);

	public static ResolvableType forArrayComponent(@NonNull ResolvableType componentType) {
		Class<?> arrayClass = Array.newInstance(componentType.getRawType(), 0).getClass();
		return forClassWithGenerics(arrayClass, componentType);
	}

	public static <T> GenericType<Class<T>> forClassWithGenerics(Class<T> type, Type... generics) {
		GenericType<Class<T>> genericTypeProvider = new GenericType<>(type);
		genericTypeProvider.setActualTypeArguments(toResolvableTypes(genericTypeProvider, generics));
		return genericTypeProvider;
	}

	public static <T extends Type> GenericType<T> forType(T type, TypeVariableResolver typeVariableResolver) {
		GenericType<T> genericTypeProvider = new GenericType<>(type);
		genericTypeProvider.setTypeVariableResolver(typeVariableResolver);
		return genericTypeProvider;
	}

	public static ResolvableType forType(Type type) {
		if (type instanceof ResolvableType) {
			return (ResolvableType) type;
		}
		return forType(type, null);
	}

	public static ResolvableType[] toResolvableTypes(TypeVariableResolver typeVariableResolver, Type... types) {
		if (types == null || types.length == 0) {
			return EMPTY_TYPES_ARRAY;
		}

		ResolvableType[] typeProviders = new ResolvableType[types.length];
		for (int i = 0; i < typeProviders.length; i++) {
			typeProviders[i] = forType(types[i], typeVariableResolver);
		}
		return typeProviders;
	}

	default ResolvableType as(Class<?> type) {
		if (this == NONE) {
			return NONE;
		}
		Class<?> resolved = getRawType();
		if (resolved == null || resolved == type) {
			return this;
		}
		for (ResolvableType interfaceType : getInterfaces()) {
			ResolvableType interfaceAsType = interfaceType.as(type);
			if (interfaceAsType != NONE) {
				return interfaceAsType;
			}
		}
		return getSuperType().as(type);
	}

	default ResolvableType getActualTypeArgument(int... indexes) {
		ResolvableType[] generics = getActualTypeArguments();
		if (indexes == null || indexes.length == 0) {
			return (generics.length == 0 ? NONE : generics[0]);
		}
		ResolvableType generic = this;
		for (int index : indexes) {
			generics = generic.getActualTypeArguments();
			if (index < 0 || index >= generics.length) {
				return NONE;
			}
			generic = generics[index];
		}
		return generic;
	}

	default boolean hasActualTypeArguments() {
		Type type = getType();
		if (type instanceof ParameterizedType) {
			return ((ParameterizedType) type).getActualTypeArguments().length != 0;
		}

		if (type instanceof TypeVariable) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			ResolvableType resolvableType = resolveTypeVariable(typeVariable);
			if (resolvableType != null) {
				return resolvableType.hasActualTypeArguments();
			}
		}
		return false;
	}

	@Override
	default ResolvableType[] getActualTypeArguments() {
		Type type = getType();
		if (type instanceof ParameterizedType) {
			return toResolvableTypes(this, ((ParameterizedType) type).getActualTypeArguments());
		}

		if (type instanceof TypeVariable) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			ResolvableType resolvableType = resolveTypeVariable(typeVariable);
			if (resolvableType != null) {
				return resolvableType.getActualTypeArguments();
			}
		}
		return EMPTY_TYPES_ARRAY;
	}

	default ResolvableType getComponentType() {
		Type type = getType();
		if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			return forType(componentType, this);
		}

		Class<?> rawType = getRawType();
		if (rawType.isArray()) {
			return forType(rawType.getComponentType(), this);
		}
		return NONE;
	}

	default ResolvableType[] getInterfaces() {
		Class<?> resolved = getRawType();
		if (resolved == null) {
			return EMPTY_TYPES_ARRAY;
		}

		Type[] genericIfcs = resolved.getGenericInterfaces();
		return toResolvableTypes(this, genericIfcs);
	}

	@Override
	default ResolvableType[] getLowerBounds() {
		Type type = getType();
		if (type instanceof WildcardType) {
			return toResolvableTypes(this, ((WildcardType) type).getLowerBounds());
		} else if (type instanceof TypeVariable) {
			return toResolvableTypes(this, ((TypeVariable<?>) type).getBounds());
		}
		return EMPTY_TYPES_ARRAY;
	}

	default ResolvableType getNested(int nestingLevel) {
		return getNested(nestingLevel, null);
	}

	default ResolvableType getNested(int nestingLevel, Map<Integer, Integer> typeIndexesPerLevel) {
		ResolvableType result = this;
		for (int i = 2; i <= nestingLevel; i++) {
			if (result.isArray()) {
				result = result.getComponentType();
			} else {
				// Handle derived types
				while (result != ResolvableType.NONE && !result.hasActualTypeArguments()) {
					result = result.getSuperType();
				}
				Integer index = (typeIndexesPerLevel != null ? typeIndexesPerLevel.get(i) : null);
				index = (index == null ? result.getActualTypeArguments().length - 1 : index);
				result = result.getActualTypeArgument(index);
			}
		}
		return result;
	}

	@Override
	default ResolvableType getOwnerType() {
		Type type = getType();
		if (type instanceof ParameterizedType) {
			return forType(((ParameterizedType) type).getOwnerType());
		}
		return null;
	}

	@Override
	default Class<?> getRawType() {
		Type type = getType();
		if (type instanceof ParameterizedType) {
			type = ((ParameterizedType) type).getRawType();
		}

		if (type instanceof TypeVariable) {
			ResolvableType resolved = resolveTypeVariable((TypeVariable<?>) type);
			if (resolved != null) {
				return resolved.getRawType();
			}
		}
		return type instanceof Class ? (Class<?>) type : Object.class;
	}

	default ResolvableType getSuperType() {
		Class<?> resolved = getRawType();
		if (resolved == null) {
			return NONE;
		}

		try {
			Type superclass = resolved.getGenericSuperclass();
			if (superclass == null) {
				return NONE;
			}
			return forType(superclass, this);
		} catch (TypeNotPresentException ex) {
			return NONE;
		}
	}

	Type getType();

	@Override
	default String getTypeName() {
		if (isArray()) {
			return getComponentType() + "[]";
		}
		if (this == NONE) {
			return "?";
		}

		String rawName = getRawType().getName();
		if (hasActualTypeArguments()) {
			return rawName + '<' + Arrays.asList(getActualTypeArguments()).stream().map(Type::getTypeName)
					.collect(Collectors.joining(", ")) + '>';
		}
		return rawName;
	}

	@Override
	default ResolvableType[] getUpperBounds() {
		Type type = getType();
		if (type instanceof WildcardType) {
			return toResolvableTypes(this, ((WildcardType) type).getUpperBounds());
		} else if (type instanceof TypeVariable) {
			return toResolvableTypes(this, ((TypeVariable<?>) type).getBounds());
		}
		return EMPTY_TYPES_ARRAY;
	}

	default boolean isArray() {
		return getType() instanceof GenericArrayType || getRawType().isArray();
	}

	default boolean isAssignableFrom(ResolvableType other) {
		if (isArray()) {
			return other.isArray() && getComponentType().isAssignableFrom(other.getComponentType());
		}

		for (ResolvableType leftBound : getLowerBounds()) {
			for (ResolvableType rightBound : other.getLowerBounds()) {
				if (!leftBound.isAssignableFrom(rightBound)) {
					return false;
				}
			}
		}

		for (ResolvableType leftBound : getUpperBounds()) {
			for (ResolvableType rightBound : other.getUpperBounds()) {
				if (!rightBound.isAssignableFrom(leftBound)) {
					return false;
				}
			}
		}

		if (getType() instanceof Class ? !ObjectUtils.equals(getRawType(), other.getRawType())
				: !ClassUtils.isAssignable(getRawType(), other.getRawType())) {
			return false;
		}

		if (hasActualTypeArguments()) {
			ResolvableType[] actualTypeArguments = getActualTypeArguments();
			ResolvableType[] otherActualTypeArguments = other.as(getRawType()).getActualTypeArguments();
			if (actualTypeArguments.length != otherActualTypeArguments.length) {
				return false;
			}

			for (int i = 0; i < actualTypeArguments.length; i++) {
				if (!actualTypeArguments[i].isAssignableFrom(otherActualTypeArguments[i])) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	default ResolvableType resolveTypeVariable(TypeVariable<?> typeVariable) {
		TypeVariable<?>[] typeParameters = getRawType().getTypeParameters();
		if (typeParameters.length != 0) {
			for (int i = 0; i < typeParameters.length; i++) {
				if (StringUtils.equals(typeParameters[i].getName(), typeVariable.getName())) {
					ResolvableType resolved = getActualTypeArgument(i);
					if (resolved != null && resolved != NONE) {
						return resolved;
					}
				}
			}
		}

		ResolvableType ownerType = getOwnerType();
		if (ownerType != null) {
			ResolvableType resolved = ownerType.resolveTypeVariable(typeVariable);
			if (resolved != null && resolved != NONE) {
				return resolved;
			}
		}

		for (ResolvableType bound : getUpperBounds()) {
			ResolvableType resolved = bound.resolveTypeVariable(typeVariable);
			if (resolved != null && resolved != NONE) {
				return resolved;
			}
		}

		for (ResolvableType bound : getLowerBounds()) {
			ResolvableType resolved = bound.resolveTypeVariable(typeVariable);
			if (resolved != null && resolved != NONE) {
				return resolved;
			}
		}
		return null;
	}
}
