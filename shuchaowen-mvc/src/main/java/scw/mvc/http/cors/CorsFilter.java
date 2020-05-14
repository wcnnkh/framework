package scw.mvc.http.cors;

import scw.beans.BeanFactory;
import scw.core.instance.annotation.Configuration;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.service.Filter;
import scw.mvc.service.FilterChain;
import scw.net.http.server.cors.CorsConfig;
import scw.net.http.server.cors.CorsConfigFactory;
import scw.value.property.PropertyFactory;

@Configuration(order = CorsFilter.ORDER)
public final class CorsFilter implements Filter {
	public static final int ORDER = 1000;

	private final CorsConfigFactory crossDomainDefinitionFactory;

	public CorsFilter(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		this(MVCUtils.getCorsConfigFactory(beanFactory, propertyFactory));
	}

	public CorsFilter(CorsConfigFactory crossDomainDefinitionFactory) {
		this.crossDomainDefinitionFactory = crossDomainDefinitionFactory;
	}

	public Object doFilter(Channel channel, FilterChain chain) throws Throwable {
		if (crossDomainDefinitionFactory != null) {
			CorsConfig corsConfig = crossDomainDefinitionFactory.getCorsConfig(channel.getRequest());
			if (corsConfig != null) {
				MVCUtils.responseCrossDomain(corsConfig, channel.getResponse());
			}
		}
		return chain.doFilter(channel);
	}
}
