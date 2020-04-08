package scw.mvc;

public interface AsyncControl {
	Channel getChannel();

	void start();

	void start(long timeout);

	boolean isStarted();

	void complete();

	boolean isCompleted();
	
	void addListener(AsyncListener asyncListener);
}
