package io.basc.framework.lang.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.StringUtils;

@FunctionalInterface
public interface TypeProvider extends ParameterizedType, WildcardType, TypeVariableResolver {
	public static final TypeProvider[] EMPTY_TYPES_ARRAY = new TypeProvider[0];
	public static final TypeProvider NONE = new GenericTypeProvider<>(Object.class);

	public static TypeProvider forType(Type type) {
		if (type instanceof TypeProvider) {
			return (TypeProvider) type;
		}
		return forType(type, null);
	}

	public static <T extends Type> GenericTypeProvider<T> forType(T type, TypeVariableResolver typeVariableResolver) {
		GenericTypeProvider<T> genericTypeProvider = new GenericTypeProvider<>(type);
		genericTypeProvider.setTypeVariableResolver(typeVariableResolver);
		return genericTypeProvider;
	}

	public static <T> GenericTypeProvider<Class<T>> forClassWithGenerics(Class<T> type, Type... generics) {
		GenericTypeProvider<Class<T>> genericTypeProvider = new GenericTypeProvider<>(type);
		genericTypeProvider.setActualTypeArguments(toTypeProviders(genericTypeProvider, generics));
		return genericTypeProvider;
	}

	public static TypeProvider[] toTypeProviders(TypeVariableResolver typeVariableResolver, Type... types) {
		if (types == null || types.length == 0) {
			return EMPTY_TYPES_ARRAY;
		}

		TypeProvider[] typeProviders = new TypeProvider[types.length];
		for (int i = 0; i < typeProviders.length; i++) {
			typeProviders[i] = forType(types[i], typeVariableResolver);
		}
		return typeProviders;
	}

	Type getType();

	@Override
	default TypeProvider[] getLowerBounds() {
		Type type = getType();
		if (type instanceof WildcardType) {
			return toTypeProviders(this, ((WildcardType) type).getLowerBounds());
		} else if (type instanceof TypeVariable) {
			return toTypeProviders(this, ((TypeVariable<?>) type).getBounds());
		}
		return EMPTY_TYPES_ARRAY;
	}

	@Override
	default TypeProvider[] getUpperBounds() {
		Type type = getType();
		if (type instanceof WildcardType) {
			return toTypeProviders(this, ((WildcardType) type).getUpperBounds());
		} else if (type instanceof TypeVariable) {
			return toTypeProviders(this, ((TypeVariable<?>) type).getBounds());
		}
		return EMPTY_TYPES_ARRAY;
	}

	@Override
	default Class<?> getRawType() {
		Type type = getType();
		if (type instanceof ParameterizedType) {
			type = ((ParameterizedType) type).getRawType();
		}
		return type instanceof Class ? (Class<?>) type : Object.class;
	}

	@Override
	default TypeProvider resolveTypeVariable(TypeVariable<?> typeVariable) {
		TypeVariable<?>[] typeParameters = getRawType().getTypeParameters();
		if (typeParameters.length != 0) {
			for (int i = 0; i < typeParameters.length; i++) {
				if (StringUtils.equals(typeParameters[i].getName(), typeVariable.getName())) {
					TypeProvider resolved = getActualTypeArgument(i);
					if (resolved != null && resolved != NONE) {
						return resolved;
					}
				}
			}
		}

		TypeProvider resolved = getOwnerType().resolveTypeVariable(typeVariable);
		if (resolved != null && resolved != NONE) {
			return resolved;
		}

		for (TypeProvider bound : getUpperBounds()) {
			resolved = bound.resolveTypeVariable(typeVariable);
			if (resolved != null && resolved != NONE) {
				return resolved;
			}
		}

		for (TypeProvider bound : getLowerBounds()) {
			resolved = bound.resolveTypeVariable(typeVariable);
			if (resolved != null && resolved != NONE) {
				return resolved;
			}
		}
		return null;
	}

	@Override
	default TypeProvider getOwnerType() {
		Type type = getType();
		if (type instanceof ParameterizedType) {
			return forType(((ParameterizedType) type).getOwnerType());
		}
		return null;
	}

	@Override
	default String getTypeName() {
		return getType().getTypeName();
	}

	default boolean hasActualTypeArguments() {
		Type type = getType();
		if (type instanceof ParameterizedType) {
			return ((ParameterizedType) type).getActualTypeArguments().length != 0;
		}
		return getActualTypeArguments().length != 0;
	}

	default TypeProvider getActualTypeArgument(int... indexes) {
		TypeProvider[] generics = getActualTypeArguments();
		if (indexes == null || indexes.length == 0) {
			return (generics.length == 0 ? NONE : generics[0]);
		}
		TypeProvider generic = this;
		for (int index : indexes) {
			generics = generic.getActualTypeArguments();
			if (index < 0 || index >= generics.length) {
				return NONE;
			}
			generic = generics[index];
		}
		return generic;
	}

	@Override
	default TypeProvider[] getActualTypeArguments() {
		Type type = getType();
		Type[] actualTypeArguments = EMPTY_TYPES_ARRAY;
		if (type instanceof ParameterizedType) {
			actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
		}
		return toTypeProviders(this, actualTypeArguments);
	}

	default TypeProvider getSuperType() {
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

	default TypeProvider[] getInterfaces() {
		Class<?> resolved = getRawType();
		if (resolved == null) {
			return EMPTY_TYPES_ARRAY;
		}

		Type[] genericIfcs = resolved.getGenericInterfaces();
		return toTypeProviders(this, genericIfcs);
	}

	default TypeProvider as(Class<?> type) {
		if (this == NONE) {
			return NONE;
		}
		Class<?> resolved = getRawType();
		if (resolved == null || resolved == type) {
			return this;
		}
		for (TypeProvider interfaceType : getInterfaces()) {
			TypeProvider interfaceAsType = interfaceType.as(type);
			if (interfaceAsType != NONE) {
				return interfaceAsType;
			}
		}
		return getSuperType().as(type);
	}

	default boolean isArray() {
		return getType() instanceof GenericArrayType || getRawType().isArray();
	}

	default TypeProvider getComponentType() {
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

	default boolean isAssignableFrom(TypeProvider other) {
		if (isArray()) {
			return other.isArray() && getComponentType().isAssignableFrom(other.getComponentType());
		}

		for (TypeProvider leftBound : getLowerBounds()) {
			for (TypeProvider rightBound : other.getLowerBounds()) {
				if (!leftBound.isAssignableFrom(rightBound)) {
					return false;
				}
			}
		}

		for (TypeProvider leftBound : getUpperBounds()) {
			for (TypeProvider rightBound : other.getUpperBounds()) {
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
			TypeProvider[] actualTypeArguments = getActualTypeArguments();
			TypeProvider[] otherActualTypeArguments = other.as(getRawType()).getActualTypeArguments();
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
}
