package io.basc.framework.net.mvc;

import java.util.Map.Entry;

import io.basc.framework.execution.Function;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.net.pattern.RequestPattern;
import io.basc.framework.net.pattern.RequestPatternRegistry;
import io.basc.framework.net.pattern.factory.DefaultRequestPatternFactory;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.Service;
import io.basc.framework.net.server.convert.DefaultServerMessageConverter;
import io.basc.framework.net.server.dispatch.Dispatcher;
import io.basc.framework.observe.register.ElementRegistration;
import io.basc.framework.util.Registrations;
import io.basc.framework.util.element.Elements;

public class Controller extends RequestPatternRegistry<Action> implements Dispatcher {
	private final DefaultRequestPatternFactory requestPatternFactory = new DefaultRequestPatternFactory();
	private final DefaultServerMessageConverter serverMessageConverter = new DefaultServerMessageConverter();

	public ElementRegistration<Entry<RequestPattern, Action>> registerAction(Action action) {
		return register(action, action);
	}

	public Registrations<ElementRegistration<Entry<RequestPattern, Action>>> registerFunction(Function function,
			Parameters parameters) {
		if (!requestPatternFactory.test(function)) {
			return Registrations.empty();
		}

		Elements<RequestPattern> requestPatterns = requestPatternFactory.getRequestPatterns(function, parameters);
		Elements<Action> actions = requestPatterns.map((requestPattern) -> {
			Action action = new Action(function, requestPattern, serverMessageConverter);
			return action;
		});

		Elements<ElementRegistration<Entry<RequestPattern, Action>>> registrations = actions
				.map((action) -> registerAction(action));
		return new Registrations<>(registrations);
	}

	@Override
	public Service dispatch(ServerRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
}
