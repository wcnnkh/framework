package scw.web.apache.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.message.InputMessage;
import scw.net.message.multipart.MultipartMessage;
import scw.net.message.multipart.MultipartMessageResolver;

public class ApacheMultipartMessageResolver implements MultipartMessageResolver {
	private static Logger logger = LoggerFactory.getLogger(ApacheMultipartMessageResolver.class);
	static {
		org.apache.commons.fileupload.FileItem.class.getName();
	}

	private ApacheFileUpload fileUpload;

	public ApacheMultipartMessageResolver() {
		this.fileUpload = new ApacheFileUpload(new DiskFileItemFactory());
	}

	public ApacheFileUpload getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(ApacheFileUpload fileUpload) {
		this.fileUpload = fileUpload;
	}

	private static class InternalRequestContext implements RequestContext {
		private InputMessage inputMessage;

		public InternalRequestContext(InputMessage inputMessage) {
			this.inputMessage = inputMessage;
		}

		public String getCharacterEncoding() {
			return inputMessage.getContentType().getCharsetName();
		}

		public String getContentType() {
			return inputMessage.getContentType().toString();
		}

		public int getContentLength() {
			return (int) inputMessage.getContentLength();
		}

		public InputStream getInputStream() throws IOException {
			return inputMessage.getInputStream();
		}

	}

	@Override
	public boolean isMultipart(InputMessage inputMessage) {
		return ApacheFileUpload.isMultipartContent(new InternalRequestContext(inputMessage));
	}

	@Override
	public List<MultipartMessage> resolve(InputMessage inputMessage) throws IOException {
		List<org.apache.commons.fileupload.FileItem> list;
		try {
			list = fileUpload.parseRequest(new InternalRequestContext(inputMessage));
		} catch (FileUploadException e) {
			throw new IOException(e);
		}

		List<MultipartMessage> fileItems = new ArrayList<MultipartMessage>(list.size());
		for (org.apache.commons.fileupload.FileItem fileItem : list) {
			if (fileItem.isFormField()) {
				logger.debug("form表单字段name={}", fileItem.getFieldName());
			} else {
				logger.debug("form表单文件[name={}, size={}, fileName={}]", fileItem.getFieldName(), fileItem.getSize(),
						fileItem.getName());
			}
			fileItems.add(new ApacheMultipartMessage(fileItem));
		}
		return fileItems;
	}
}
