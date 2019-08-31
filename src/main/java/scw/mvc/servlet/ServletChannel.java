package scw.mvc.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.mvc.support.ParameterChannel;

public interface ServletChannel extends ParameterChannel {
	ServletRequest getRequest();

	ServletResponse getResponse();
}
