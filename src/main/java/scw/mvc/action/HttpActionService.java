package scw.mvc.action;

import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;

public abstract class HttpActionService extends ActionService {
	public abstract HttpAction getAction(HttpChannel httpChannel);

	public Action getAction(Channel channel) {
		if (channel instanceof HttpChannel) {
			return getAction((HttpChannel) channel);
		}

		return null;
	}

	public abstract void scanning(HttpAction action, HttpControllerConfig httpControllerConfig);
}
