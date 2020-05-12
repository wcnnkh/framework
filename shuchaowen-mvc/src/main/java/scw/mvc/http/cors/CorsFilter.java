package scw.mvc.http.cors;

import scw.beans.BeanFactory;
import scw.core.instance.annotation.Configuration;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.service.FilterChain;
import scw.mvc.service.HttpFilter;
import scw.value.property.PropertyFactory;

@Configuration(order=CorsFilter.ORDER)
public final class CorsFilter extends HttpFilter{
	public static final int ORDER = 1000;
	
	private final CorsConfigFactory crossDomainDefinitionFactory;
	
	public CorsFilter(BeanFactory beanFactory, PropertyFactory propertyFactory){
		this(MVCUtils.getCorsConfigFactory(beanFactory, propertyFactory));
	}
	
	public CorsFilter(CorsConfigFactory crossDomainDefinitionFactory){
		this.crossDomainDefinitionFactory = crossDomainDefinitionFactory;
	}
	
	@Override
	protected Object doHttpFilter(HttpChannel channel, FilterChain chain)
			throws Throwable {
		if(crossDomainDefinitionFactory != null){
			CorsConfig corsConfig = crossDomainDefinitionFactory
					.getCorsConfig(channel);
			if (corsConfig != null) {
				MVCUtils.responseCrossDomain(corsConfig, channel.getResponse());
			}
		}
		return chain.doFilter(channel);
	}
}
