package io.basc.framework.rpc.http;

import io.basc.framework.rpc.http.annotation.AnnotationHttpRemoteResolver;

public class DefaultHttpRemoteResolvers extends HttpRemoteResolvers {
	public DefaultHttpRemoteResolvers() {
		setLast(new AnnotationHttpRemoteResolver());
	}
}
