package io.basc.framework.web;

public interface ServerAsyncControl {
	void start();

	void start(long timeout);

	boolean isStarted();

	void complete();

	boolean isCompleted();

	void addListener(ServerAsyncListener serverHttpAsyncListener);
}
