package io.basc.framework.net.server;

import java.io.IOException;
import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class FilterChain implements Server {
	@NonNull
	private final Iterator<? extends Filter> iterator;
	private Server next;

	@Override
	public void service(ServerRequest request, ServerResponse response) throws IOException, ServerException {
		if (iterator.hasNext()) {
			iterator.next().doFilter(request, response, this);
		} else if (next != null) {
			next.service(request, response);
		}
	}
}
