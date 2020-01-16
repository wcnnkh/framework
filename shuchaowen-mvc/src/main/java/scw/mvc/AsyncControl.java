package scw.mvc;

public interface AsyncControl {
	Request getRequest();

	Response getResponse();

	void start();

	void start(long timeout);

	boolean isStarted();

	void complete();

	boolean isCompleted();
}
