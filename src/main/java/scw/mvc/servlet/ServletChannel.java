package scw.mvc.servlet;

import scw.mvc.ParameterChannel;
import scw.mvc.RequestResponseModelChannel;

public interface ServletChannel<R extends ServletRequest, P extends ServletResponse> extends ParameterChannel, RequestResponseModelChannel<R, P> {
}
