package scw.mvc.action.notfound;

import scw.mvc.Channel;
import scw.mvc.action.notfound.adapter.MultiNotfoundAdapter;
import scw.mvc.http.HttpChannel;
import scw.mvc.service.FilterChain;
import scw.net.http.HttpMethod;

public class DefaultNotfoundService extends MultiNotfoundAdapter implements
		NotFoundService {
	private static final long serialVersionUID = 1L;

	public Object notfound(Channel channel, FilterChain filterChain)
			throws Throwable {
		if (channel instanceof HttpChannel) {
			HttpChannel httpChannel = (HttpChannel) channel;
			// 不处理options的请求，因为这可能是跨域听处理
			if (HttpMethod.OPTIONS == httpChannel.getRequest().getMethod()) {
				return filterChain.doFilter(httpChannel);
			}
		}

		if (isAdapter(channel)) {
			return notfound(channel);
		}

		return filterChain.doFilter(channel);
	}
}
