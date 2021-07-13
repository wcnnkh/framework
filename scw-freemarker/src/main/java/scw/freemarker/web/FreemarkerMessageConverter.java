package scw.freemarker.web;

import java.io.IOException;
import java.io.PrintWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import scw.context.annotation.Provider;
import scw.convert.TypeDescriptor;
import scw.core.Ordered;
import scw.core.utils.StringUtils;
import scw.http.HttpHeaders;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.MimeTypes;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;
import scw.net.message.convert.MessageConvertException;
import scw.net.message.convert.MessageConverter;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.message.WebMessageConverter;
import scw.web.message.WebMessagelConverterException;
import scw.web.model.Page;
import scw.web.model.PageMessageConverter;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class FreemarkerMessageConverter extends PageMessageConverter implements WebMessageConverter, MessageConverter {
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
		Page page = (Page) body;
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
	protected boolean canWrite(Page page) {
		return StringUtils.endsWithIgnoreCase(page.getName(), ".ftl")
				|| StringUtils.endsWithIgnoreCase(page.getName(), ".html");
	}

	@Override
	protected void writePage(TypeDescriptor type, Page page, ServerHttpRequest request, ServerHttpResponse response)
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
		return body != null && body instanceof Page && canWrite((Page) body);
	}
}
