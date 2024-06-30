package io.basc.framework.net.client;

import java.io.IOException;

import io.basc.framework.net.pattern.RequestPattern;

public interface ClientRequestFactory {
	boolean canCreated(RequestPattern requestPattern);

	ClientRequest createRequest(RequestPattern requestPattern) throws IOException;
}
