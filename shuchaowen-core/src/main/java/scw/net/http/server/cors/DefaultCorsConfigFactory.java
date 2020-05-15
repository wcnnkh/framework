package scw.net.http.server.cors;

import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.mvc.MVCUtils;
import scw.value.property.PropertyFactory;

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
