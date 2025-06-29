package run.soeasy.framework.core.type;

import java.lang.reflect.TypeVariable;
import java.util.Map;

import run.soeasy.framework.core.domain.Wrapper;

@FunctionalInterface
public interface ResolvableTypeWrapper<W extends ResolvableType> extends ResolvableType, Wrapper<W> {
	@Override
	default ResolvableType[] getActualTypeArguments() {
		return getSource().getActualTypeArguments();
	}

	@Override
	default ResolvableType getComponentType() {
		return getSource().getComponentType();
	}

	@Override
	default ResolvableType[] getLowerBounds() {
		return getSource().getLowerBounds();
	}

	@Override
	default ResolvableType getOwnerType() {
		return getSource().getOwnerType();
	}

	@Override
	default Class<?> getRawType() {
		return getSource().getRawType();
	}

	@Override
	default ResolvableType[] getUpperBounds() {
		return getSource().getUpperBounds();
	}

	@Override
	default boolean hasActualTypeArguments() {
		return getSource().hasActualTypeArguments();
	}

	@Override
	default boolean isArray() {
		return getSource().isArray();
	}

	@Override
	default ResolvableType resolveTypeVariable(TypeVariable<?> typeVariable) {
		return getSource().resolveTypeVariable(typeVariable);
	}

	@Override
	default ResolvableType as(Class<?> type) {
		return getSource().as(type);
	}

	@Override
	default ResolvableType getActualTypeArgument(int... indexes) {
		return getSource().getActualTypeArgument(indexes);
	}

	@Override
	default ResolvableType[] getInterfaces() {
		return getSource().getInterfaces();
	}

	@Override
	default ResolvableType getNested(int nestingLevel) {
		return getSource().getNested(nestingLevel);
	}

	@Override
	default ResolvableType getNested(int nestingLevel, Map<Integer, Integer> typeIndexesPerLevel) {
		return getSource().getNested(nestingLevel, typeIndexesPerLevel);
	}

	@Override
	default ResolvableType getSuperType() {
		return getSource().getSuperType();
	}

	@Override
	default String getTypeName() {
		return getSource().getTypeName();
	}

	@Override
	default boolean isAssignableFrom(ResolvableType other) {
		return getSource().isAssignableFrom(other);
	}
}
