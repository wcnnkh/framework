package io.basc.framework.rpc.http;

import io.basc.framework.rpc.http.annotation.AnnotationHttpRemoteUriResolver;

public class DefaultHttpRemoteUriResolvers extends HttpRemoteUriResolvers {
	public DefaultHttpRemoteUriResolvers() {
		setAfterService(new AnnotationHttpRemoteUriResolver());
	}
}
