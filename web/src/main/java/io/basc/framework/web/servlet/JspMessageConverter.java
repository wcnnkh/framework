package io.basc.framework.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.message.model.ModelAndView;
import io.basc.framework.web.message.model.ModelAndViewMessageConverter;

public class JspMessageConverter extends ModelAndViewMessageConverter {

	@Override
	protected boolean canWrite(ModelAndView page) {
		return StringUtils.endsWithIgnoreCase(page.getName(), ".jsp");
	}

	@Override
	protected void writePage(TypeDescriptor type, ModelAndView page, ServerHttpRequest request,
			ServerHttpResponse response) throws IOException, WebMessagelConverterException {
		HttpServletRequest servletRequest = XUtils.getDelegate(request, HttpServletRequest.class);
		HttpServletResponse servletResponse = XUtils.getDelegate(response, HttpServletResponse.class);
		if (servletRequest == null || servletResponse == null) {
			throw new WebMessagelConverterException(type, page, request, null);
		}

		try {
			ServletUtils.forward(servletRequest, servletResponse, page.getName());
		} catch (ServletException e) {
			throw new WebMessagelConverterException(type, page, request, e);
		}
	}

	@Override
	public ClientHttpRequest write(ClientHttpRequest request, ParameterDescriptor parameterDescriptor, Object parameter)
			throws IOException, WebMessagelConverterException {
		return request;
	}
}
