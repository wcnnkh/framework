package scw.web.servlet.action;

import scw.web.servlet.Request;
import scw.web.servlet.Response;

public interface Action {
	void doAction(Request request, Response response) throws Throwable;
}
