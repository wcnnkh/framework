package scw.mvc;

import scw.core.instance.annotation.Configuration;

@Configuration(order = Integer.MIN_VALUE)
public class DefaultNotfoundService implements NotFoundService {

	public Object notfound(HttpChannel httpChannel) throws Throwable {
		httpChannel.getLogger().warn("not found：{}", httpChannel.toString());
		httpChannel.getResponse().sendError(404, "not found action");
		return null;
	}
}
