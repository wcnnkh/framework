package io.basc.framework.web.servlet.http;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessagelConverterException;
import io.basc.framework.web.model.Page;
import io.basc.framework.web.model.PageMessageConverter;
import io.basc.framework.web.servlet.ServletUtils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class JspMessageConverter extends PageMessageConverter {

	@Override
	protected boolean canWrite(Page page) {
		return StringUtils.endsWithIgnoreCase(page.getName(), ".jsp");
	}

	@Override
	protected void writePage(TypeDescriptor type, Page page, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
		HttpServletRequest servletRequest = XUtils.getDelegate(request, HttpServletRequest.class);
		HttpServletResponse servletResponse = XUtils.getDelegate(response, HttpServletResponse.class);
		if (servletRequest == null || servletResponse == null) {
			throw new WebMessagelConverterException(type, page, request, null);
		}

		try {
			ServletUtils.jsp(servletRequest, servletResponse, page.getName());
		} catch (ServletException e) {
			throw new WebMessagelConverterException(type, page, request, e);
		}
	}
}
