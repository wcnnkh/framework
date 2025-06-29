package run.soeasy.framework.core.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import lombok.Getter;
import lombok.NonNull;

/**
 * 可解析的参数类型
 * 
 * @author soeasy.run
 *
 */
@Getter
public class ResolvableParameterizedType extends AbstractResolvableType<ParameterizedType> {

	public ResolvableParameterizedType(@NonNull ParameterizedType type, TypeVariableResolver typeVariableResolver) {
		super(type, typeVariableResolver);
	}

	@Override
	public boolean hasActualTypeArguments() {
		return getType().getActualTypeArguments().length != 0;
	}

	@Override
	public ResolvableType[] getActualTypeArguments() {
		return ResolvableType.toResolvableTypes(this.getTypeVariableResolver(), getType().getActualTypeArguments());
	}

	@Override
	public boolean isArray() {
		return false;
	}

	@Override
	public ResolvableType getComponentType() {
		return NONE;
	}

	@Override
	public ResolvableType[] getLowerBounds() {
		return EMPTY_TYPES_ARRAY;
	}

	@Override
	public ResolvableType getOwnerType() {
		Type type = getType().getOwnerType();
		return type == null ? null : ResolvableType.forType(type, this.getTypeVariableResolver());
	}

	@Override
	public Class<?> getRawType() {
		Type rawType = getType().getRawType();
		if (rawType == null) {
			return null;
		}
		return rawType instanceof Class ? ((Class<?>) rawType) : Object.class;
	}

	@Override
	public ResolvableType[] getUpperBounds() {
		return EMPTY_TYPES_ARRAY;
	}
}
