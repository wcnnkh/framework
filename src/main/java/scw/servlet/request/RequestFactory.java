package scw.servlet.request;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.servlet.Request;
import scw.servlet.beans.RequestBeanFactory;

public interface RequestFactory {
	Request format(RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException;
}
