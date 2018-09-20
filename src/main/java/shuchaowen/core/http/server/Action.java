package shuchaowen.core.http.server;

public interface Action {
	void doAction(Request request, Response response) throws Throwable;
}
