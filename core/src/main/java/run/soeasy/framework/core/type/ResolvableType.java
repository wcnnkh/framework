package run.soeasy.framework.core.type;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.CollectionUtils;

public interface ResolvableType extends ParameterizedType, WildcardType, TypeVariableResolver {
	public static final ResolvableType[] EMPTY_TYPES_ARRAY = new ResolvableType[0];
	public static final ResolvableTypeFactory RESOLVABLE_TYPE_FACTORY = CollectionUtils
			.unknownSizeStream(ServiceLoader.load(ResolvableTypeFactory.class).iterator()).findFirst()
			.orElse(DefaultResolvableTypeFactory.INSTANCE);
	public static final ResolvableType NONE = new NoneType(Object.class);

	public static ResolvableType[] toResolvableTypes(TypeVariableResolver typeVariableResolver, Type... types) {
		if (types == null || types.length == 0) {
			return ResolvableType.EMPTY_TYPES_ARRAY;
		}

		ResolvableType[] typeProviders = new ResolvableType[types.length];
		for (int i = 0; i < typeProviders.length; i++) {
			typeProviders[i] = forType(types[i], typeVariableResolver);
		}
		return typeProviders;
	}

	public static ResolvableType forType(@NonNull Type type) {
		return forType(type, null);
	}

	public static ResolvableType forType(@NonNull Type type, TypeVariableResolver resolver) {
		if (type instanceof ResolvableType) {
			return (ResolvableType) type;
		}
		return RESOLVABLE_TYPE_FACTORY.createResolvableType(type, resolver);
	}

	public static ResolvableType forArrayComponent(@NonNull ResolvableType componentType) {
		Class<?> arrayClass = Array.newInstance(componentType.getRawType(), 0).getClass();
		return forClassWithGenerics(arrayClass, componentType);
	}

	public static GenericType forClassWithGenerics(Class<?> type, Type... generics) {
		GenericType genericType = new GenericType(type);
		genericType.setActualTypeArguments(generics);
		return genericType;
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

	boolean hasActualTypeArguments();

	@Override
	ResolvableType[] getActualTypeArguments();

	boolean isArray();

	ResolvableType getComponentType();

	default ResolvableType[] getInterfaces() {
		Class<?> resolved = getRawType();
		if (resolved == null) {
			return EMPTY_TYPES_ARRAY;
		}

		Type[] genericIfcs = resolved.getGenericInterfaces();
		return toResolvableTypes(this, genericIfcs);
	}

	@Override
	ResolvableType[] getLowerBounds();

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
	ResolvableType getOwnerType();

	@Override
	Class<?> getRawType();

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
			ResolvableType[] actualTypeArguments = getActualTypeArguments();
			return rawName + '<' + Arrays.asList(actualTypeArguments).stream().map(Type::getTypeName)
					.collect(Collectors.joining(", ")) + '>';
		}
		return rawName;
	}

	@Override
	ResolvableType[] getUpperBounds();

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

		if (hasActualTypeArguments() ? !ClassUtils.isAssignable(getRawType(), other.getRawType())
				: !ObjectUtils.equals(getRawType(), other.getRawType())) {
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
