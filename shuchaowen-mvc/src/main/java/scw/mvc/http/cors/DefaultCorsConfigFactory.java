package scw.mvc.http.cors;

import scw.beans.annotation.Bean;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;
import scw.util.value.property.PropertyFactory;

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
