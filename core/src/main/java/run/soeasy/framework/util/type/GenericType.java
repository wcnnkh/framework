package run.soeasy.framework.util.type;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import lombok.NonNull;
import run.soeasy.framework.util.Assert;

public class GenericType<T extends Type> implements ResolvableType {
	private ResolvableType[] actualTypeArguments;
	private ResolvableType ownerType;
	private Class<?> rawType;
	@NonNull
	private final T type;
	private TypeVariableResolver typeVariableResolver;

	public GenericType(@NonNull T type) {
		Assert.isTrue(type != this, () -> "Cannot be recycled " + type + " construct, please use TypeProvider#forType");
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

		if (type instanceof TypeVariable) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			ResolvableType resolved = resolveTypeVariable(typeVariable);
			if (resolved != null) {
				return resolved.getRawType();
			}
		}
		return ResolvableType.super.getRawType();
	}

	@Override
	public final Type getType() {
		return type;
	}

	@Override
	public String getTypeName() {
		if (type instanceof Class) {
			StringBuilder sb = new StringBuilder();
			sb.append(type.getTypeName());
			ResolvableType[] typeProviders = getActualTypeArguments();
			if (typeProviders.length > 0) {
				sb.append("<");
				for (int i = 0; i < typeProviders.length; i++) {
					if (i != 0) {
						sb.append(", ");
					}
					sb.append(typeProviders[i].getTypeName());
				}
				sb.append(">");
			}
			return sb.toString();
		}
		return type.getTypeName();
	}

	@Override
	public ResolvableType resolveTypeVariable(TypeVariable<?> typeVariable) {
		return typeVariableResolver == null ? ResolvableType.super.resolveTypeVariable(typeVariable)
				: typeVariableResolver.resolveTypeVariable(typeVariable);
	}

	public void setActualTypeArguments(ResolvableType[] actualTypeArguments) {
		this.actualTypeArguments = actualTypeArguments;
	}

	public void setOwnerType(ResolvableType ownerType) {
		Assert.isTrue(ownerType != this, "Cannot be recycled " + ownerType);
		this.ownerType = ownerType;
	}

	public void setRawType(Class<?> rawType) {
		this.rawType = rawType;
	}

	public void setTypeVariableResolver(TypeVariableResolver typeVariableResolver) {
		Assert.isTrue(typeVariableResolver != this, "Cannot be recycled " + typeVariableResolver);
		this.typeVariableResolver = typeVariableResolver;
	}

	@Override
	public String toString() {
		return this.getTypeName();
	}
}
