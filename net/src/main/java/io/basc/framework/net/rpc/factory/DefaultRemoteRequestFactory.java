package io.basc.framework.net.rpc.factory;

import java.io.IOException;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.param.Parameters;
import io.basc.framework.net.client.ClientRequest;
import io.basc.framework.net.client.factory.DefaultClientRequestFactory;
import io.basc.framework.net.pattern.RequestPattern;
import io.basc.framework.net.pattern.factory.DefaultRequestPatternFactory;
import io.basc.framework.util.Elements;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class DefaultRemoteRequestFactory extends ConfigurableRemoteRequestFactory {
	@NonNull
	private RemoteRequestFactory groundRemoteRequestFactory = GlobalRemoteRequestFactory.getInstance();
	private final DefaultRequestPatternFactory requestPatternFactory = new DefaultRequestPatternFactory();
	private final DefaultClientRequestFactory clientRequestFactory = new DefaultClientRequestFactory();

	@Override
	public boolean test(Function function) {
		return super.test(function) || requestPatternFactory.test(function)
				|| groundRemoteRequestFactory.test(function);
	}

	@Override
	public ClientRequest createRequest(Function function, Parameters parameters) throws IOException {
		if (super.test(function)) {
			return super.createRequest(function, parameters);
		}

		Elements<RequestPattern> elements = requestPatternFactory.getRequestPatterns(function, parameters);
		for (RequestPattern pattern : elements) {
			if (clientRequestFactory.canCreated(pattern)) {
				return clientRequestFactory.createRequest(pattern);
			}
		}
		return groundRemoteRequestFactory.createRequest(function, parameters);
	}
}
