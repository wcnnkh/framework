package io.basc.framework.net.pattern;

import java.net.URI;

public interface UriPattern extends PathPattern {
	URI getURI();

	@Override
	default String getPath() {
		return getURI().getPath();
	}
}
