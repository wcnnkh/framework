package scw.mvc.http;

public interface HttpChannelFactory<R extends HttpRequest, P extends HttpResponse> {
	HttpChannel<R, P> getHttpChannel(R request, P response);
}
