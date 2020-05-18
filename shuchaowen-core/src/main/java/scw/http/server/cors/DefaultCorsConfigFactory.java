package scw.http.server.cors;

import scw.http.server.ServerHttpRequest;
import scw.value.property.PropertyFactory;

public class DefaultCorsConfigFactory implements CorsConfigFactory{
	private CorsConfig defaultConfig;
	
	public DefaultCorsConfigFactory(PropertyFactory propertyFactory){
		// 默认开启跨域
		if(propertyFactory.getValue("server.http.cross-domain", boolean.class, true)){
			this.defaultConfig = new CorsConfig(propertyFactory);
		}
	}
	
	public CorsConfig getCorsConfig(ServerHttpRequest request) {
		return defaultConfig;
	}

}
