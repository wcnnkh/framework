package scw.web.servlet.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.context.annotation.Provider;
import scw.convert.TypeDescriptor;
import scw.core.Ordered;
import scw.core.utils.StringUtils;
import scw.util.XUtils;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.message.WebMessagelConverterException;
import scw.web.model.Page;
import scw.web.model.PageMessageConverter;
import scw.web.servlet.ServletUtils;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class JspMessageConverter extends PageMessageConverter {

	@Override
	protected boolean canWrite(Page page) {
		return StringUtils.endsWithIgnoreCase(page.getName(), ".jsp");
	}

	@Override
	protected void writePage(TypeDescriptor type, Page page, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
		HttpServletRequest servletRequest = XUtils.getTarget(request, HttpServletRequest.class);
		HttpServletResponse servletResponse = XUtils.getTarget(response, HttpServletResponse.class);
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
