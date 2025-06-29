package run.soeasy.framework.core.type;

import java.lang.reflect.WildcardType;

import lombok.NonNull;

public class ResolvableWildcardType extends AbstractResolvableType<WildcardType> {

	public ResolvableWildcardType(@NonNull WildcardType type, TypeVariableResolver typeVariableResolver) {
		super(type, typeVariableResolver);
	}

	@Override
	public ResolvableType[] getLowerBounds() {
		return ResolvableType.toResolvableTypes(this.getTypeVariableResolver(), getType().getLowerBounds());
	}

	@Override
	public ResolvableType[] getUpperBounds() {
		return ResolvableType.toResolvableTypes(this.getTypeVariableResolver(), getType().getUpperBounds());
	}

	@Override
	public boolean hasActualTypeArguments() {
		return false;
	}

	@Override
	public ResolvableType[] getActualTypeArguments() {
		return EMPTY_TYPES_ARRAY;
	}

	@Override
	public boolean isArray() {
		return false;
	}

	@Override
	public ResolvableType getComponentType() {
		return null;
	}

	@Override
	public ResolvableType getOwnerType() {
		return null;
	}

	@Override
	public Class<?> getRawType() {
		return Object.class;
	}
}
