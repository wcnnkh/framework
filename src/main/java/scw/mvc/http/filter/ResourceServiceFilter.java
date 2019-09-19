package scw.mvc.http.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import scw.core.utils.StringUtils;
import scw.io.IOUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.FilterChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpFilter;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;

public final class ResourceServiceFilter extends HttpFilter {
	private static Logger logger = LoggerFactory.getLogger(ResourceServiceFilter.class);
	private final String filePath;
	private final String[] path;

	public ResourceServiceFilter(String filePath, String[] path) {
		this.filePath = filePath;
		this.path = path;
		logger.info("root:{}", filePath);
		logger.info("path:{}", Arrays.toString(path));
	}

	private boolean checkPath(HttpRequest httpRequest) {
		if(!"GET".equals(httpRequest.getMethod())){
			return false;
		}
		
		if (path == null || path.length == 0) {
			return false;
		}

		for (String p : path) {
			if (StringUtils.test(httpRequest.getRequestPath(), p)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object doFilter(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse, FilterChain chain)
			throws Throwable {
		if (!checkPath(httpRequest)) {
			return chain.doFilter(channel);
		}

		File file = new File(filePath + httpRequest.getRequestPath());
		if (!file.exists() || !file.isFile()) {
			return chain.doFilter(channel);
		}

		outputFile(file, httpResponse);
		return null;
	}

	private void outputFile(File file, HttpResponse httpResponse) throws IOException {
		OutputStream output = null;
		FileInputStream fis = null;
		FileChannel fileChannel = null;
		MappedByteBuffer mappedByteBuffer;
		try {
			output = httpResponse.getOutputStream();
			fis = new FileInputStream(file);
			fileChannel = fis.getChannel();
			mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
			output.write(mappedByteBuffer.array());
		} finally {
			IOUtils.close(fileChannel, fis, output);
		}
	}

}
