package scw.mvc.http.cors;

import scw.beans.annotation.Bean;
import scw.core.instance.annotation.Configuration;
import scw.mvc.MVCUtils;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.cors.CorsConfig;
import scw.net.http.server.cors.CorsConfigFactory;
import scw.value.property.PropertyFactory;

@Bean(proxy=false)
@Configuration(order=Integer.MIN_VALUE)
public class DefaultCorsConfigFactory implements CorsConfigFactory{
	private CorsConfig defaultConfig;
	
	public DefaultCorsConfigFactory(PropertyFactory propertyFactory){
		if(MVCUtils.isSupportCorssDomain(propertyFactory)){
			this.defaultConfig = new CorsConfig(propertyFactory);
		}
	}
	
	public CorsConfig getCorsConfig(ServerHttpRequest request) {
		return defaultConfig;
	}

}
