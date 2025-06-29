package run.soeasy.framework.core.type;

import java.lang.reflect.Type;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * 泛型
 * 
 * @author soeasy.run
 *
 */
@Getter
@Setter
public class GenericType extends RawType {
	@NonNull
	private Type[] actualTypeArguments = EMPTY_TYPES_ARRAY;
	private Type ownerType;

	public GenericType(@NonNull Class<?> rawType) {
		super(rawType);
	}

	@Override
	public ResolvableType[] getActualTypeArguments() {
		return ResolvableType.toResolvableTypes(this.getTypeVariableResolver(), actualTypeArguments);
	}

	@Override
	public ResolvableType getOwnerType() {
		return ownerType == null ? null : ResolvableType.forType(ownerType, this.getTypeVariableResolver());
	}

	@Override
	public boolean hasActualTypeArguments() {
		return actualTypeArguments == null ? super.hasActualTypeArguments() : actualTypeArguments.length != 0;
	}
}
