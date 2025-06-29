package run.soeasy.framework.core.type;

import java.lang.reflect.Type;

import lombok.NonNull;

public interface ResolvableTypeFactory {
	@NonNull ResolvableType createResolvableType(@NonNull Type type, TypeVariableResolver resolver);
}
