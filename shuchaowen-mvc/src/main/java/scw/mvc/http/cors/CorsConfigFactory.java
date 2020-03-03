package scw.mvc.http.cors;

import scw.beans.annotation.AutoImpl;
import scw.mvc.http.HttpChannel;

@AutoImpl(DefaultCorsConfigFactory.class)
public interface CorsConfigFactory {
	CorsConfig getCorsConfig(HttpChannel httpChannel);
}
