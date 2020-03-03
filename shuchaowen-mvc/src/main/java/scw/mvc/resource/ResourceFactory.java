package scw.mvc.resource;

import scw.mvc.Request;

public interface ResourceFactory {
	Resource getResource(Request request);
}
