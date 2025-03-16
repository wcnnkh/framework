package io.basc.framework.net.client;

import io.basc.framework.net.RequestPattern;

public interface ClientRequestFactory {
	ClientRequest createRequest(RequestPattern requestPattern);
}
