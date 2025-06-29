package run.soeasy.framework.core.type;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "type")
public abstract class AbstractResolvableType<T extends Type> implements ResolvableType {
	@NonNull
	private final T type;
	private TypeVariableResolver typeVariableResolver;

	public AbstractResolvableType(@NonNull T type, TypeVariableResolver typeVariableResolver) {
		this(type);
		setTypeVariableResolver(typeVariableResolver);
	}

	public void setTypeVariableResolver(TypeVariableResolver typeVariableResolver) {
		if (typeVariableResolver != null) {
			// 循环引用检查
			TypeVariableResolver check = typeVariableResolver;
			while (check != null) {
				if (check == this) {
					throw new IllegalStateException(typeVariableResolver + " has a circular reference");
				}

				if (check instanceof AbstractResolvableType) {
					check = ((AbstractResolvableType<?>) check).typeVariableResolver;
				}
			}
		}
		this.typeVariableResolver = typeVariableResolver;
	}

	@Override
	public ResolvableType resolveTypeVariable(TypeVariable<?> typeVariable) {
		ResolvableType resolvableType = ResolvableType.super.resolveTypeVariable(typeVariable);
		if (resolvableType == null && typeVariableResolver != null) {
			resolvableType = typeVariableResolver.resolveTypeVariable(typeVariable);
		}
		return resolvableType;
	}

	@Override
	public final String toString() {
		return getTypeName();
	}
}
