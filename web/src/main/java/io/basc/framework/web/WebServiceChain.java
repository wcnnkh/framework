package io.basc.framework.web;

import java.io.IOException;
import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class WebServiceChain implements WebService {
	private final Iterator<? extends WebServiceInterceptor> iterator;
	private WebService nextChain;

	@Override
	public void service(ServerRequest serverRequest, ServerResponse serverResponse) throws IOException, WebException {
		if (iterator != null && iterator.hasNext()) {
			iterator.next().intercept(serverRequest, serverResponse, this);
		} else if (nextChain != null) {
			nextChain.service(serverRequest, serverResponse);
		}
	}
}
