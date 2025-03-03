package io.basc.framework.net.resource;

import java.io.IOException;

import io.basc.framework.net.server.ServerException;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerResponse;
import io.basc.framework.net.server.Service;
import io.basc.framework.util.io.load.ResourceLoader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResourceService implements Service {
	@NonNull
	private final ResourceLoader resourceLoader;

	@Override
	public void service(ServerRequest request, ServerResponse response) throws IOException, ServerException {
		// TODO Auto-generated method stub

	}

}
