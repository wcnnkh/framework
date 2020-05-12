package scw.mvc.resource;

import scw.mvc.ServerRequest;

public interface ResourceFactory {
	Resource getResource(ServerRequest serverRequest);
}
