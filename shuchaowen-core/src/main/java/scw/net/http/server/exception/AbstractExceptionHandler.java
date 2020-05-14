package scw.net.http.server.exception;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import scw.net.http.server.ServerHttpRequest;

public abstract class AbstractExceptionHandler extends LinkedList<Class<? extends Throwable>>
		implements HttpServerExceptionHandler {
	private static final long serialVersionUID = 1L;

	public Collection<Class<? extends Throwable>> getSupports() {
		return Collections.unmodifiableCollection(this);
	}

	public boolean isSupport(ServerHttpRequest request, Throwable error) {
		for (Class<? extends Throwable> errorType : this) {
			if (errorType.isAssignableFrom(error.getClass())) {
				return true;
			}
		}
		return false;
	}
}
