package scw.mvc;

public interface Action<T extends Channel> {
	void doAction(T channel) throws Throwable;
}
