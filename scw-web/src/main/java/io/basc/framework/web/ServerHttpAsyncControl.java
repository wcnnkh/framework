package io.basc.framework.web;

public interface ServerHttpAsyncControl {
	void start();

	void start(long timeout);

	boolean isStarted();

	void complete();

	boolean isCompleted();
	
	void addListener(ServerHttpAsyncListener serverHttpAsyncListener);
}
