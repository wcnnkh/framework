package scw.mvc.servlet.http;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.mvc.ParameterDefinition;
import scw.mvc.ParameterFilterChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpParameterFilter;

public class HttpServletParameterFilter extends HttpParameterFilter {

	@Override
	public Object filter(HttpChannel httpChannel, ParameterDefinition parameterDefinition, ParameterFilterChain chain)
			throws Throwable {
		if (ServletRequest.class.isAssignableFrom(parameterDefinition.getType())) {
			return httpChannel.getRequest();
		} else if (ServletResponse.class.isAssignableFrom(parameterDefinition.getType())) {
			return httpChannel.getResponse();
		}

		return chain.doFilter(httpChannel, parameterDefinition);
	}

}
