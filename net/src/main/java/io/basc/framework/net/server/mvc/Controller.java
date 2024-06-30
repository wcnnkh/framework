package io.basc.framework.net.server.mvc;

import io.basc.framework.net.MimeTypes;

public interface Controller {
	String getPath();

	MimeTypes getConsumes();

	MimeTypes getProduces();
}
