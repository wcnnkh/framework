package run.soeasy.framework.core.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import lombok.NonNull;

public class DefaultResolvableTypeFactory implements ResolvableTypeFactory {
	public static final DefaultResolvableTypeFactory INSTANCE = new DefaultResolvableTypeFactory();

	@Override
	public @NonNull ResolvableType createResolvableType(@NonNull Type type, TypeVariableResolver resolver) {
		if (type instanceof ParameterizedType) {
			return new ResolvableParameterizedType((ParameterizedType) type, resolver);
		} else if (type instanceof WildcardType) {
			return new ResolvableWildcardType((WildcardType) type, resolver);
		} else if (type instanceof TypeVariable) {
			return new ResolvableTypeVariable((TypeVariable<?>) type, resolver);
		} else if (type instanceof Class) {
			return new RawType((Class<?>) type, resolver);
		} else if (type instanceof GenericArrayType) {
			return new ArrayType((GenericArrayType) type, resolver);
		}
		return new NoneType(type);
	}

}
