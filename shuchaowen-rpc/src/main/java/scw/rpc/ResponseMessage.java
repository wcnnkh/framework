package scw.rpc;

import java.lang.reflect.Type;

import scw.lang.Nullable;

public interface ResponseMessage {
	Object getValue();

	Throwable getError();

	Class<?> getValueType();

	@Nullable
	Type getValueGenericType();
}
