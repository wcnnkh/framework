package scw.mvc.http;

import scw.mvc.ParameterChannel;
import scw.mvc.RequestResponseModelChannel;

public interface HttpChannel extends ParameterChannel, RequestResponseModelChannel<HttpRequest, HttpResponse>{
}
