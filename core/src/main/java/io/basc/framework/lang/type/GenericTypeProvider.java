package io.basc.framework.lang.type;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import io.basc.framework.util.Assert;
import lombok.NonNull;

public class GenericTypeProvider<T extends Type> implements TypeProvider {
	private TypeProvider[] actualTypeArguments;
	private TypeProvider ownerType;
	private Class<?> rawType;
	@NonNull
	private final T type;
	private TypeVariableResolver typeVariableResolver;

	public GenericTypeProvider(@NonNull T type) {
		Assert.isTrue(type != this, () -> "Cannot be recycled " + type + " construct, please use TypeProvider#forType");
		this.type = type;
	}

	@Override
	public TypeProvider[] getActualTypeArguments() {
		return actualTypeArguments == null ? TypeProvider.super.getActualTypeArguments() : actualTypeArguments;
	}

	@Override
	public TypeProvider getOwnerType() {
		return ownerType != null ? TypeProvider.super.getOwnerType() : ownerType;
	}

	@Override
	public Class<?> getRawType() {
		if (rawType != null) {
			return rawType;
		}

		if (type instanceof TypeVariable) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			TypeProvider resolved = resolveTypeVariable(typeVariable);
			if (resolved != null) {
				return resolved.getRawType();
			}
		}
		return TypeProvider.super.getRawType();
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
			TypeProvider[] typeProviders = getActualTypeArguments();
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
	public TypeProvider resolveTypeVariable(TypeVariable<?> typeVariable) {
		return typeVariableResolver == null ? TypeProvider.super.resolveTypeVariable(typeVariable)
				: typeVariableResolver.resolveTypeVariable(typeVariable);
	}

	public void setActualTypeArguments(TypeProvider[] actualTypeArguments) {
		this.actualTypeArguments = actualTypeArguments;
	}

	public void setOwnerType(TypeProvider ownerType) {
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
