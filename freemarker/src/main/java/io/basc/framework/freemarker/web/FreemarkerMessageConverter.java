package io.basc.framework.freemarker.web;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.mvc.message.WebMessageConverter;
import io.basc.framework.mvc.message.WebMessagelConverterException;
import io.basc.framework.mvc.model.ModelAndView;
import io.basc.framework.mvc.model.ModelAndViewMessageConverter;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.OutputMessage;
import io.basc.framework.net.message.convert.MessageConvertException;
import io.basc.framework.net.message.convert.MessageConverter;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

import java.io.IOException;
import java.io.PrintWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class FreemarkerMessageConverter extends ModelAndViewMessageConverter implements WebMessageConverter, MessageConverter {
	private final Configuration configuration;

	public FreemarkerMessageConverter(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public MimeTypes getSupportMimeTypes() {
		return MimeTypes.EMPTY;
	}

	@Override
	public boolean canRead(TypeDescriptor type, MimeType contentType) {
		return false;
	}

	@Override
	public void write(TypeDescriptor typeDescriptor, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException {
		ModelAndView page = (ModelAndView) body;
		HttpHeaders headers = page.getHeaders();
		if (headers != null) {
			outputMessage.getHeaders().putAll(headers);
		}

		if (outputMessage.getContentType() == null) {
			outputMessage.setContentType(MimeTypeUtils.TEXT_HTML);
		}

		Template template = configuration.getTemplate(page.getName(), outputMessage.getCharacterEncoding());
		try {
			template.process(page, new PrintWriter(outputMessage.getOutputStream()));
		} catch (TemplateException e) {
			throw new MessageConvertException(page.getName(), e);
		}
	}

	@Override
	public Object read(TypeDescriptor typeDescriptor, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		return null;
	}

	@Override
	protected boolean canWrite(ModelAndView page) {
		return StringUtils.endsWithIgnoreCase(page.getName(), ".ftl")
				|| StringUtils.endsWithIgnoreCase(page.getName(), ".html");
	}

	@Override
	protected void writePage(TypeDescriptor type, ModelAndView page, ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, WebMessagelConverterException {
		Template template = configuration.getTemplate(page.getName(), response.getCharacterEncoding());
		try {
			template.process(page, response.getWriter());
		} catch (TemplateException e) {
			throw new WebMessagelConverterException(page.getName(), e);
		}
	}

	@Override
	public boolean canWrite(TypeDescriptor typeDescriptor, Object body, MimeType contentType) {
		return body != null && body instanceof ModelAndView && canWrite((ModelAndView) body);
	}
}
