package shuchaowen.web.servlet.request;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shuchaowen.web.servlet.Request;
import shuchaowen.web.servlet.bean.RequestBeanFactory;

public interface RequestFactory {
	Request format(RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException;
}
