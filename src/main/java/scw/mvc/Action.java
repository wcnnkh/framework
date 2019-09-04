package scw.mvc;

public interface Action<T extends Channel> {
	Object doAction(T channel) throws Throwable;
}
