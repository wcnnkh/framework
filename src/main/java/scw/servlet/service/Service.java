package scw.servlet.service;

import scw.servlet.Request;
import scw.servlet.Response;

public interface Service {
	
	void service(Request request, Response response, ServiceChain serviceChain) throws Throwable;
	
}
