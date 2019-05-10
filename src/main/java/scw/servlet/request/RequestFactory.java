package scw.servlet.request;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.json.JSONParseSupport;
import scw.servlet.Request;
import scw.servlet.beans.RequestBeanFactory;

public interface RequestFactory {
	Request format(JSONParseSupport jsonParseSupport, RequestBeanFactory requestBeanFactory,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException;
}
