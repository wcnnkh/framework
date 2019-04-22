package scw.servlet.service;

import scw.servlet.Request;
import scw.servlet.Response;

public interface ServiceChain {
	void service(Request request, Response response) throws Throwable;
}
