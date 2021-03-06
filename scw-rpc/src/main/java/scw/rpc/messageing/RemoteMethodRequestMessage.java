package scw.rpc.messageing;

import java.lang.reflect.Method;

public interface RemoteMethodRequestMessage extends RemoteRequestMessage {
	Class<?> getTargetClass();

	Method getMethod();

	Object[] getArgs();
}
