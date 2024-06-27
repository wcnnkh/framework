package io.basc.framework.net.server.mvc;

import java.util.function.Predicate;

import io.basc.framework.net.MimeTypes;
import io.basc.framework.net.Request;

public interface Controller extends Predicate<Request>, Comparable<Controller> {
	MimeTypes getProduces();

	@Override
	boolean test(Request request);

	@Override
	int compareTo(Controller controller);
}
