package run.soeasy.framework.net.resource;

import java.io.IOException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.net.server.ServerException;
import run.soeasy.framework.net.server.ServerRequest;
import run.soeasy.framework.net.server.ServerResponse;
import run.soeasy.framework.net.server.Service;
import run.soeasy.framework.util.io.load.ResourceLoader;

@RequiredArgsConstructor
public class ResourceService implements Service {
	@NonNull
	private final ResourceLoader resourceLoader;

	@Override
	public void service(ServerRequest request, ServerResponse response) throws IOException, ServerException {
		// TODO Auto-generated method stub

	}

}
