package io.basc.framework.net.mvc;

import java.util.List;
import java.util.Map;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.Parameters;
import io.basc.framework.net.pattern.RequestPattern;
import io.basc.framework.net.pattern.RequestPatternRegistry;
import io.basc.framework.net.pattern.factory.DefaultRequestPatternFactory;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.Server;
import io.basc.framework.net.server.convert.DefaultServerMessageConverter;
import io.basc.framework.net.server.dispatch.Dispatcher;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.exchange.Registrations;
import io.basc.framework.util.function.Supplier;
import io.basc.framework.util.register.container.EntryRegistration;
import lombok.NonNull;

public class Controller extends RequestPatternRegistry<Action> implements Dispatcher {
	private final DefaultRequestPatternFactory requestPatternFactory = new DefaultRequestPatternFactory();
	private final DefaultServerMessageConverter serverMessageConverter = new DefaultServerMessageConverter();

	public Controller(
			@NonNull Supplier<? extends Map<RequestPattern, EntryRegistration<RequestPattern, Action>>, ? extends RuntimeException> containerSource) {
		super(containerSource);
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
			return action;
		});

		List<Registration> registrations = actions.map((action) -> registerAction(action)).toList();
		return Registrations.forList(registrations);
	}

	@Override
	public Server dispatch(ServerRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
}
