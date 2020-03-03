package scw.mvc.http.cors;

import scw.beans.annotation.Bean;
import scw.core.PropertyFactory;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;

@Bean(proxy=false)
public class DefaultCorsConfigFactory implements CorsConfigFactory{
	private CorsConfig defaultConfig;
	
	public DefaultCorsConfigFactory(PropertyFactory propertyFactory){
		if(MVCUtils.isSupportCorssDomain(propertyFactory)){
			this.defaultConfig = new CorsConfig(propertyFactory);
		}
	}
	
	public CorsConfig getCorsConfig(HttpChannel httpChannel) {
		return defaultConfig;
	}

}
