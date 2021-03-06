package scw.rpc.messageing;

import java.io.Serializable;

public interface RemoteResponseMessage extends MessageHeaders, Serializable {
	Throwable getThrowable();

	Object getBody();
}
