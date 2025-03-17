package run.soeasy.framework.net.server;

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
public class ServerFilterChain implements Service {
	@NonNull
	private final Iterator<? extends ServerFilter> iterator;
	private Service next;

	@Override
	public void service(ServerRequest request, ServerResponse response) throws IOException, ServerException {
		if (iterator.hasNext()) {
			iterator.next().doFilter(request, response, this);
		} else if (next != null) {
			next.service(request, response);
		}
	}
}
