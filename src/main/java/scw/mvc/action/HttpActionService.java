package scw.mvc.action;

import java.util.Collection;

import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;

public abstract class HttpActionService extends AbstractActionService {
	private final Collection<ActionFilter> actionFilters;

	public HttpActionService(Collection<ActionFilter> actionFilters) {
		this.actionFilters = actionFilters;
	}

	@Override
	public final Collection<ActionFilter> getActionFilters() {
		return actionFilters;
	}

	public abstract HttpAction getAction(HttpChannel httpChannel);

	public Action<Channel> getAction(Channel channel) {
		if (channel instanceof HttpChannel) {
			return getAction((HttpChannel) channel);
		}

		return null;
	}

	public abstract void scanning(HttpAction action, HttpControllerConfig httpControllerConfig);
}
