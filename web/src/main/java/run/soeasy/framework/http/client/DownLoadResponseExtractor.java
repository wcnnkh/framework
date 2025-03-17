package run.soeasy.framework.http.client;

import java.io.File;
import java.io.IOException;

import run.soeasy.framework.http.HttpRequest;
import run.soeasy.framework.http.HttpStatus;
import run.soeasy.framework.net.ContentDisposition;
import run.soeasy.framework.net.InetUtils;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.logging.LogManager;
import run.soeasy.framework.util.logging.Logger;

public class DownLoadResponseExtractor implements ClientHttpResponseExtractor<File> {
	private static Logger logger = LogManager.getLogger(DownLoadResponseExtractor.class);

	public static final ClientHttpResponseExtractor<File> INSTANCE = new DownLoadResponseExtractor();
	private String rootPath;

	public File execute(HttpRequest request, ClientHttpResponse response) throws IOException {
		if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.NOT_MODIFIED) {
			logger.error("Unable to download:{}, status:{}, statusText:{}", request.getURI(),
					response.getRawStatusCode(), response.getStatusText());
			return null;
		}

		ContentDisposition contentDisposition = response.getHeaders().getContentDisposition();
		String fileName = contentDisposition == null ? null : contentDisposition.getFilename();
		if (StringUtils.isEmpty(fileName)) {
			fileName = InetUtils.getFilename(request.getURI().getPath());
		}

		String filePath = StringUtils
				.cleanPath(StringUtils.isEmpty(rootPath) ? fileName : (rootPath + File.separator + fileName));
		if (logger.isDebugEnabled()) {
			logger.debug("{} download to {}", request.getURI(), filePath);
		}

		File file = new File(filePath);
		try {
			response.transferTo(file);
		} finally {
			file.deleteOnExit();
		}
		return file;
	}
}