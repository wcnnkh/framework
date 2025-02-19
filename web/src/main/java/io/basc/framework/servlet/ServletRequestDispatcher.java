package io.basc.framework.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import io.basc.framework.net.server.ServerException;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerRequestDispatcher;
import io.basc.framework.net.server.ServerResponse;
import io.basc.framework.util.function.Wrapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ServletRequestDispatcher implements ServerRequestDispatcher {
	@NonNull
	private final RequestDispatcher requestDispatcher;

	@Override
	public void forward(ServerRequest request, ServerResponse response) throws IOException, ServerException {
		ServletRequest servletRequest = Wrapper.unwrap(request, ServletRequest.class);
		ServletResponse servletResponse = Wrapper.unwrap(response, ServletResponse.class);
		if (servletRequest == null || servletResponse == null) {
			throw new UnsupportedOperationException("This is not a servlet object");
		}

		try {
			requestDispatcher.forward(servletRequest, servletResponse);
		} catch (ServletException e) {
			throw new ServerException(e);
		}
	}

	@Override
	public void include(ServerRequest request, ServerResponse response) throws IOException {
		ServletRequest servletRequest = Wrapper.unwrap(request, ServletRequest.class);
		ServletResponse servletResponse = Wrapper.unwrap(response, ServletResponse.class);
		if (servletRequest == null || servletResponse == null) {
			throw new UnsupportedOperationException("This is not a servlet object");
		}

		try {
			requestDispatcher.include(servletRequest, servletResponse);
		} catch (ServletException e) {
			throw new ServerException(e);
		}
	}

}
