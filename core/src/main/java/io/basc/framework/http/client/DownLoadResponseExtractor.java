package io.basc.framework.http.client;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import io.basc.framework.http.ContentDisposition;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.io.FileUtils;
import io.basc.framework.io.support.TemporaryFile;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.InetUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;

public class DownLoadResponseExtractor implements ClientHttpResponseExtractor<File> {
	private static Logger logger = LoggerFactory.getLogger(DownLoadResponseExtractor.class);
	private final URI url;

	public DownLoadResponseExtractor(URI url) {
		this.url = url;
	}

	public URI getUrl() {
		return url;
	}

	public File execute(ClientHttpResponse response) throws IOException {
		if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.NOT_MODIFIED) {
			logger.error("Unable to download:{}, status:{}, statusText:{}", url, response.getRawStatusCode(),
					response.getStatusText());
			return null;
		}

		ContentDisposition contentDisposition = response.getHeaders().getContentDisposition();
		String fileName = contentDisposition == null ? null : contentDisposition.getFilename();
		if (StringUtils.isEmpty(fileName)) {
			fileName = InetUtils.getFilename(url.getPath());
		}

		TemporaryFile file = new TemporaryFile(
				FileUtils.getTempDirectory() + File.separator + XUtils.getUUID() + File.separator + fileName);
		if (logger.isDebugEnabled()) {
			logger.debug("{} download to {}", url, file.getPath());
		}

		try {
			FileUtils.copyInputStreamToFile(response.getInputStream(), file);
		} finally {
			file.deleteOnExit();
		}
		return file;
	}
}