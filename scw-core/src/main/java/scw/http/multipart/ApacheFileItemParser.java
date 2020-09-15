package scw.http.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import scw.http.HttpInputMessage;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class ApacheFileItemParser implements FileItemParser {
	private static Logger logger = LoggerUtils.getLogger(ApacheFileItemParser.class);
	static {
		org.apache.commons.fileupload.FileItem.class.getName();
	}

	private FileUpload fileUpload;

	public ApacheFileItemParser() {
		this.fileUpload = new FileUpload(new DiskFileItemFactory());
	}

	public FileUpload getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(FileUpload fileUpload) {
		this.fileUpload = fileUpload;
	}

	public List<FileItem> parse(HttpInputMessage httpInputMessage) throws IOException {
		List<org.apache.commons.fileupload.FileItem> list;
		try {
			list = fileUpload.parseRequest(new HttpRequestContext(httpInputMessage));
		} catch (FileUploadException e) {
			throw new IOException(e);
		}

		List<FileItem> fileItems = new ArrayList<FileItem>(list.size());
		for (org.apache.commons.fileupload.FileItem fileItem : list) {
			if (fileItem.isFormField()) {
				logger.debug("form表单字段name={}", fileItem.getFieldName());
			} else {
				logger.debug("form表单文件[name={}, size={}, fileName={}]", fileItem.getFieldName(), fileItem.getSize(),
						fileItem.getName());
			}
			fileItems.add(new ApacheFileItem(fileItem));
		}
		return fileItems;
	}

	private static class HttpRequestContext implements RequestContext {
		private HttpInputMessage httpInputMessage;

		public HttpRequestContext(HttpInputMessage httpInputMessage) {
			this.httpInputMessage = httpInputMessage;
		}

		public String getCharacterEncoding() {
			return httpInputMessage.getContentType().getCharsetName();
		}

		public String getContentType() {
			return httpInputMessage.getContentType().toString();
		}

		public int getContentLength() {
			return (int) httpInputMessage.getContentLength();
		}

		public InputStream getInputStream() throws IOException {
			return httpInputMessage.getBody();
		}

	}
}
