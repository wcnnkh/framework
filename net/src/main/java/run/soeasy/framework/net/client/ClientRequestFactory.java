package run.soeasy.framework.net.client;

import run.soeasy.framework.net.RequestPattern;

public interface ClientRequestFactory {
	ClientRequest createRequest(RequestPattern requestPattern);
}
