package io.basc.framework.net.server;

public interface ServerAsyncEvent {

	ServerAsyncControl getServerAsyncControl();

	Throwable getThrowable();

}
