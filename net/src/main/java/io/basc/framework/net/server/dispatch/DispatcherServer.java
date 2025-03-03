package io.basc.framework.net.server.dispatch;

import java.io.IOException;
import java.util.List;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.Parameters;
import io.basc.framework.net.RequestPattern;
import io.basc.framework.net.convert.MessageConverters;
import io.basc.framework.net.convert.UriParameterConverters;
import io.basc.framework.net.convert.support.DefaultMessageConverters;
import io.basc.framework.net.convert.support.DefaultUriParameterConverters;
import io.basc.framework.net.pattern.DefaultRequestPatternFactory;
import io.basc.framework.net.pattern.RequestPatternFactory;
import io.basc.framework.net.server.Service;
import io.basc.framework.net.server.ServerException;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerResponse;
import io.basc.framework.net.server.Server;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.exchange.Registrations;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DispatcherServer extends Server {
	private ErrorHandler errorHandler;
	private GroundServer groundServer;
	private final RequestPatternFactory requestPatternFactory = new DefaultRequestPatternFactory();
	private final MessageConverters serverMessageConverter = new DefaultMessageConverters();
	private final UriParameterConverters uriParameterConverters = new DefaultUriParameterConverters();

	public Registration registerAction(Action action) {
		return register(action.getRequestPattern(), action);
	}

	public Registration registerFunction(Function function, Parameters parameters) {
		if (!requestPatternFactory.test(function)) {
			return Registrations.empty();
		}

		Elements<RequestPattern> requestPatterns = requestPatternFactory.getRequestPatterns(function, parameters);
		Elements<Action> actions = requestPatterns.map((requestPattern) -> {
			Action action = new Action(function, requestPattern, serverMessageConverter, uriParameterConverters);
			action.setErrorHandler(this.errorHandler);
			return action;
		});

		List<Registration> registrations = actions.map((action) -> registerAction(action)).toList();
		return Registrations.forList(registrations);
	}

	@Override
	public void doFilter(ServerRequest request, ServerResponse response, Service server)
			throws IOException, ServerException {
		if (server == null) {
			server = groundServer;
		}

		try {
			super.doFilter(request, response, server);
		} catch (IOException e) {
			throw e;
		} catch (Throwable e) {
			if (errorHandler == null) {
				throw new ServerException(e);
			}
			errorHandler.doError(request, response, server, e);
		}
	}
}
