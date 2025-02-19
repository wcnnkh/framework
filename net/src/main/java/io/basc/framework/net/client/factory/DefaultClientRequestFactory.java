package io.basc.framework.net.client.factory;

import java.io.IOException;

import io.basc.framework.net.RequestPattern;
import io.basc.framework.net.client.ClientRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class DefaultClientRequestFactory extends ConfigurableClientRequestFactory {
	@NonNull
	private ClientRequestFactory groundClientRequestFactory = GlobalClientRequestFactory.getInstance();

	@Override
	public boolean canCreated(RequestPattern requestPattern) {
		return super.canCreated(requestPattern) || groundClientRequestFactory.canCreated(requestPattern);
	}

	@Override
	public ClientRequest createRequest(RequestPattern requestPattern) throws IOException {
		if (super.canCreated(requestPattern)) {
			return super.createRequest(requestPattern);
		}
		return groundClientRequestFactory.createRequest(requestPattern);
	}
}
