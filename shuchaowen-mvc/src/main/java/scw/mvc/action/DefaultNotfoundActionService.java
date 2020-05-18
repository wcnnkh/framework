package scw.mvc.action;

import scw.core.instance.annotation.Configuration;
import scw.mvc.HttpChannel;

@Configuration(order = Integer.MIN_VALUE)
public class DefaultNotfoundActionService implements NotFoundActionService {

	public Object notfound(HttpChannel httpChannel) throws Throwable {
		httpChannel.getLogger().warn("not found：{}", httpChannel.toString());
		httpChannel.getResponse().sendError(404, "not found action");
		return null;
	}
}
