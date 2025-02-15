package io.basc.framework.net.server;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Filterable implements Filter {
	@NonNull
	private final Iterable<? extends Filter> filters;

	@Override
	public void doFilter(ServerRequest request, ServerResponse response, Server chain)
			throws IOException, ServerException {
		FilterChain filterChain = new FilterChain(filters.iterator(), chain);
		filterChain.service(request, response);
	}
}