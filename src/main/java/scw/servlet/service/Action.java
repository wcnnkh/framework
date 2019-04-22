package scw.servlet.service;

import scw.servlet.Request;
import scw.servlet.Response;

public interface Action {
	void doAction(Request request, Response response) throws Throwable;
}
