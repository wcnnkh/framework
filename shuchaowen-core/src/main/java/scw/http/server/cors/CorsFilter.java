package scw.http.server.cors;

import java.io.IOException;

import scw.beans.BeanFactory;
import scw.core.utils.StringUtils;
import scw.http.server.HttpService;
import scw.http.server.HttpServiceFilter;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.value.property.PropertyFactory;

public final class CorsFilter implements HttpServiceFilter {
	private final CorsConfigFactory corsConfigFactory;

	public CorsFilter(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		String beanName = propertyFactory.getString("mvc.cross-domain.factory");
		if (StringUtils.isEmpty(beanName)) {
			this.corsConfigFactory = beanFactory.isInstance(CorsConfigFactory.class)
					? beanFactory.getInstance(CorsConfigFactory.class) : new DefaultCorsConfigFactory(propertyFactory);
		} else {
			this.corsConfigFactory = beanFactory.getInstance(beanName);
		}
	}

	public CorsFilter(CorsConfigFactory corsConfigFactory) {
		this.corsConfigFactory = corsConfigFactory;
	}

	public void doFilter(ServerHttpRequest request, ServerHttpResponse response, HttpService httpService)
			throws IOException {
		if (corsConfigFactory != null) {
			CorsConfig corsConfig = corsConfigFactory.getCorsConfig(request);
			if (corsConfig != null) {
				CorsUtils.write(corsConfig, response);
			}
		}
		httpService.service(request, response);
	}
}
