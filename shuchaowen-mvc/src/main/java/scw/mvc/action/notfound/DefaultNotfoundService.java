package scw.mvc.action.notfound;

import scw.core.instance.annotation.Configuration;
import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;
import scw.mvc.service.FilterChain;
import scw.net.http.HttpMethod;

@Configuration(order = Integer.MIN_VALUE)
public class DefaultNotfoundService implements NotFoundService {

	public Object notfound(Channel channel, FilterChain filterChain) throws Throwable {
		if (ignore(channel)) {
			return filterChain.doFilter(channel);
		}

		channel.getLogger().warn("not found：{}", channel.toString());
		if (channel instanceof HttpChannel) {
			return httpNotfound((HttpChannel) channel, filterChain);
		}
		return nextNotFound(channel, filterChain);
	}

	protected Object nextNotFound(Channel channel, FilterChain filterChain) throws Throwable {
		return filterChain.doFilter(channel);
	}

	protected boolean ignore(Channel channel) {
		if (channel instanceof HttpChannel) {
			HttpChannel httpChannel = (HttpChannel) channel;
			// 不处理options的请求，因为这可能是跨域听处理
			if (HttpMethod.OPTIONS == httpChannel.getRequest().getMethod()) {
				return true;
			}
		}
		return false;
	}

	protected Object httpNotfound(HttpChannel httpChannel, FilterChain filterChain) throws Throwable {
		httpChannel.getResponse().sendError(404, "not found action");
		return null;
	}
}
