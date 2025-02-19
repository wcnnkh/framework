package io.basc.framework.net.client.factory;

import java.io.IOException;

import io.basc.framework.net.RequestPattern;
import io.basc.framework.net.client.ClientRequest;

public interface ClientRequestFactory {
	boolean canCreated(RequestPattern requestPattern);

	ClientRequest createRequest(RequestPattern requestPattern) throws IOException;
}
