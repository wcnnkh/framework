package scw.rpc.remote;



public interface RemoteResponseMessage extends RemoteMessageHeaders{
	Throwable getThrowable();

	Object getBody();
}
