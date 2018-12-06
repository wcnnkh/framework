package shuchaowen.web.servlet.action;

import shuchaowen.web.servlet.Request;
import shuchaowen.web.servlet.Response;

public interface Action {
	void doAction(Request request, Response response) throws Throwable;
}
