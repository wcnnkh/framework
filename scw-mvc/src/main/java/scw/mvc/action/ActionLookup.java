package scw.mvc.action;

import scw.beans.annotation.AopEnable;
import scw.http.server.ServerHttpRequest;

@AopEnable(false)
public interface ActionLookup {
	Action lookup(ServerHttpRequest request);
}
