package io.basc.framework.net.server.dispatch;

import java.io.IOException;
import java.util.List;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.Parameters;
import io.basc.framework.net.pattern.PathPattern;
import io.basc.framework.net.pattern.RequestPattern;
import io.basc.framework.net.pattern.factory.DefaultRequestPatternFactory;
import io.basc.framework.net.server.Server;
import io.basc.framework.net.server.ServerException;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerResponse;
import io.basc.framework.net.server.Servers;
import io.basc.framework.net.server.convert.DefaultServerMessageConverter;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.exchange.Registrations;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DispatcherServer extends Servers {
	private ErrorHandler errorHandler;
	private GroundServer groundServer;
	private final DefaultRequestPatternFactory requestPatternFactory = new DefaultRequestPatternFactory();
	private final DefaultServerMessageConverter serverMessageConverter = new DefaultServerMessageConverter();

	public Server dispatch(ServerRequest request) {
		PathPattern pathPattern = request.getPattern();
		Server server = get(pathPattern);
		if (server == null) {
			server = entries().filter((e) -> e.getKey().test(request)).map((e) -> e.getValue()).first();
		}
		return server;
	}

	public Registration registerAction(Action action) {
		return register(action, action);
	}

	public Registration registerFunction(Function function, Parameters parameters) {
		if (!requestPatternFactory.test(function)) {
			return Registrations.empty();
		}

		Elements<RequestPattern> requestPatterns = requestPatternFactory.getRequestPatterns(function, parameters);
		Elements<Action> actions = requestPatterns.map((requestPattern) -> {
			Action action = new Action(function, requestPattern, serverMessageConverter);
			action.setErrorHandler(this.errorHandler);
			return action;
		});

		List<Registration> registrations = actions.map((action) -> registerAction(action)).toList();
		return Registrations.forList(registrations);
	}

	@Override
	public void doFilter(ServerRequest request, ServerResponse response, Server server)
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
