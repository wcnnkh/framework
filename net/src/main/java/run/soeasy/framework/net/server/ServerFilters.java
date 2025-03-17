package run.soeasy.framework.net.server;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ServerFilters implements ServerFilter {
	@NonNull
	private final Iterable<? extends ServerFilter> filters;

	@Override
	public void doFilter(ServerRequest request, ServerResponse response, Service chain)
			throws IOException, ServerException {
		ServerFilterChain filterChain = new ServerFilterChain(filters.iterator(), chain);
		filterChain.service(request, response);
	}
}