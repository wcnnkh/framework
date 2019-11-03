package scw.mvc.http;

import scw.mvc.RequestResponseModelChannel;

public interface HttpChannel extends RequestResponseModelChannel<HttpRequest, HttpResponse> {
	HttpParameterRequest getHttpParameterRequest();
}
