package scw.mvc.servlet.http;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.core.parameter.ParameterConfig;
import scw.mvc.ParameterFilterChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpParameterFilter;

public class HttpServletParameterFilter extends HttpParameterFilter {

	@Override
	public Object filter(HttpChannel httpChannel, ParameterConfig parameterConfig, ParameterFilterChain chain)
			throws Throwable {
		if (ServletRequest.class.isAssignableFrom(parameterConfig.getType())) {
			return httpChannel.getRequest();
		} else if (ServletResponse.class.isAssignableFrom(parameterConfig.getType())) {
			return httpChannel.getResponse();
		}

		return chain.doFilter(httpChannel, parameterConfig);
	}

}
