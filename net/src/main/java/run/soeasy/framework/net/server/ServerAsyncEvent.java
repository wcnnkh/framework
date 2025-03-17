package run.soeasy.framework.net.server;

public interface ServerAsyncEvent {

	ServerAsyncControl getServerAsyncControl();

	Throwable getThrowable();

}
