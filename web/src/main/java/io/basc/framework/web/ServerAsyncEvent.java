package io.basc.framework.web;

public interface ServerAsyncEvent {

	ServerAsyncControl getServerAsyncControl();

	Throwable getThrowable();

}
