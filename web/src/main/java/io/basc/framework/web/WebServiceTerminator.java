package io.basc.framework.web;

public interface WebServiceTerminator extends WebService {
	boolean test(ServerRequest serverRequest);
}
