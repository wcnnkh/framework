package scw.mvc.http;

import scw.mvc.parameter.ParameterChannel;

public interface HttpChannel extends ParameterChannel {
	@SuppressWarnings("unchecked")
	HttpRequest getRequest();

	@SuppressWarnings("unchecked")
	HttpResponse getResponse();

	HttpParameterRequest getHttpParameterRequest();
}