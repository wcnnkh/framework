package scw.mvc.http;

import scw.mvc.ParameterChannel;
import scw.mvc.RequestResponseModelChannel;

public interface HttpChannel<R extends HttpRequest, P extends HttpResponse> extends ParameterChannel, RequestResponseModelChannel<R, P>{
}
