package run.soeasy.framework.core.type;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class GenericType<T extends Type> implements ResolvableType {
	private ResolvableType[] actualTypeArguments;
	private ResolvableType ownerType;
	private Class<?> rawType;
	@NonNull
	private final T type;
	private TypeVariableResolver typeVariableResolver;

	public GenericType(@NonNull T type) {
		this.type = type;
	}

	@Override
	public ResolvableType[] getActualTypeArguments() {
		return actualTypeArguments == null ? ResolvableType.super.getActualTypeArguments() : actualTypeArguments;
	}

	@Override
	public ResolvableType getOwnerType() {
		return ownerType != null ? ResolvableType.super.getOwnerType() : ownerType;
	}

	@Override
	public Class<?> getRawType() {
		if (rawType != null) {
			return rawType;
		}
		return ResolvableType.super.getRawType();
	}

	@Override
	public boolean hasActualTypeArguments() {
		return actualTypeArguments == null ? ResolvableType.super.hasActualTypeArguments()
				: actualTypeArguments.length != 0;
	}

	@Override
	public ResolvableType resolveTypeVariable(TypeVariable<?> typeVariable) {
		return typeVariableResolver == null ? ResolvableType.super.resolveTypeVariable(typeVariable)
				: typeVariableResolver.resolveTypeVariable(typeVariable);
	}

	@Override
	public final String toString() {
		return getTypeName();
	}
}
