package run.soeasy.framework.core.type;

import java.lang.reflect.TypeVariable;

import lombok.NonNull;

/**
 * 原始类型
 * 
 * @author soeasy.run
 *
 */
public class RawType extends AbstractResolvableType<Class<?>> {
	public RawType(@NonNull Class<?> type) {
		super(type);
	}

	public RawType(@NonNull Class<?> type, TypeVariableResolver typeVariableResolver) {
		super(type, typeVariableResolver);
	}

	@Override
	public final Class<?> getRawType() {
		return getType();
	}

	@Override
	public boolean hasActualTypeArguments() {
		return getType().getTypeParameters().length != 0;
	}

	@Override
	public ResolvableType[] getActualTypeArguments() {
		TypeVariable<?>[] typeVariables = getType().getTypeParameters();
		return ResolvableType.toResolvableTypes(this.getTypeVariableResolver(), typeVariables);
	}

	@Override
	public boolean isArray() {
		return getType().isArray();
	}

	@Override
	public ResolvableType getComponentType() {
		return ResolvableType.forType(getType().getComponentType(), this.getTypeVariableResolver());
	}

	@Override
	public ResolvableType[] getLowerBounds() {
		return EMPTY_TYPES_ARRAY;
	}

	@Override
	public ResolvableType getOwnerType() {
		return null;
	}

	@Override
	public ResolvableType[] getUpperBounds() {
		return EMPTY_TYPES_ARRAY;
	}
}
