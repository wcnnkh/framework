package run.soeasy.framework.core.type;

import java.lang.reflect.Type;

import lombok.NonNull;

/**
 * 无类型
 * 
 * @author soeasy.run
 *
 */
public final class NoneType extends AbstractResolvableType<Type> {

	public NoneType(@NonNull Type type) {
		super(type);
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
	public ResolvableType[] getLowerBounds() {
		return EMPTY_TYPES_ARRAY;
	}

	@Override
	public ResolvableType getOwnerType() {
		return null;
	}

	@Override
	public Class<?> getRawType() {
		return Object.class;
	}

	@Override
	public ResolvableType[] getUpperBounds() {
		return EMPTY_TYPES_ARRAY;
	}

}
