package run.soeasy.framework.core.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

import lombok.Getter;

/**
 * 数组类型
 * 
 * @author soeasy.run
 *
 */
@Getter
public class ArrayType extends AbstractResolvableType<GenericArrayType> {

	public ArrayType(GenericArrayType genericArrayType, TypeVariableResolver typeVariableResolver) {
		super(genericArrayType, typeVariableResolver);
	}

	@Override
	public ResolvableType getComponentType() {
		Type type = getType().getGenericComponentType();
		return ResolvableType.forType(type, this.getTypeVariableResolver());
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
		return true;
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
		return Object[].class;
	}

	@Override
	public ResolvableType[] getUpperBounds() {
		return EMPTY_TYPES_ARRAY;
	}
}
