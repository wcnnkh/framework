package scw.mvc.action.notfound;

import scw.core.instance.annotation.Configuration;
import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;
import scw.mvc.service.FilterChain;
import scw.net.http.HttpMethod;

@Configuration(order=Integer.MIN_VALUE)
public class DefaultNotfoundService implements NotFoundService {

	public Object notfound(Channel channel, FilterChain filterChain)
			throws Throwable {
		if (channel instanceof HttpChannel) {
			HttpChannel httpChannel = (HttpChannel) channel;
			// 不处理options的请求，因为这可能是跨域听处理
			if (HttpMethod.OPTIONS == httpChannel.getRequest().getMethod()) {
				return filterChain.doFilter(httpChannel);
			}

			return httpNotfound(httpChannel, filterChain);
		}
		return filterChain.doFilter(channel);
	}

	protected Object httpNotfound(HttpChannel httpChannel,
			FilterChain filterChain) throws Throwable {
		httpChannel.getResponse().sendError(404, "not found action");
		return null;
	}
}
