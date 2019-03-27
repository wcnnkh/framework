package scw.servlet;

public interface Action {
	void doAction(Request request, Response response) throws Throwable;
}
