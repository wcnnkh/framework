package scw.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import scw.data.ResourceStorageService;
import scw.data.StorageException;
import scw.http.HttpRequest;
import scw.http.HttpRequestEntity;
import scw.http.MediaType;
import scw.io.FileUtils;
import scw.io.IOUtils;
import scw.io.UrlResource;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.message.InputMessage;
import scw.net.uri.UriComponentsBuilder;

/**
 * 上传器
 * 
 * @author shuchaowen
 *
 */
public class Uploader implements ResourceStorageService {
	public static final String CONTROLLER = "${upload.controller:/upload}";
	private static Logger logger = LoggerFactory.getLogger(Uploader.class);
	private final UploadPolicy uploadPolicy;
	private final File directory;

	public Uploader(UploadPolicy uploadPolicy, File directory) {
		this.uploadPolicy = uploadPolicy;
		this.directory = directory;
	}

	public UploadPolicy getUploadPolicy() {
		return uploadPolicy;
	}

	@Override
	public UrlResource get(String key) throws StorageException, IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(uploadPolicy.getBaseUrl());
		sb.append("/");
		sb.append(key);
		return new UrlResource(sb.toString());
	}

	@Override
	public boolean put(String key, InputMessage input) throws StorageException,
			IOException {
		logger.info("put [{}]", key);
		File file = new File(directory, key);
		InputStream is = null;
		try {
			is = input.getBody();
			FileUtils.copyInputStreamToFile(input.getBody(), file);
		} finally {
			IOUtils.close(is);
		}
		return true;
	}

	@Override
	public boolean delete(String key) throws StorageException {
		logger.info("delete [{}]", key);
		File file = new File(directory, key);
		return file.delete();
	}

	@Override
	public boolean delete(URI uri) throws StorageException {
		logger.info("delete [{}]", uri);
		String str = uri.toString();
		if (!str.startsWith(uploadPolicy.getBaseUrl())) {
			return false;
		}

		String key = str.substring(uploadPolicy.getBaseUrl().length());
		return delete(key);
	}

	@Override
	public HttpRequest generate(String key, long expiration)
			throws StorageException {
		String sign = getUploadPolicy().getSign(key, expiration);
		URI uri = UriComponentsBuilder
				.fromUriString(
						getUploadPolicy().getBaseUrl()
								+ getUploadPolicy().getController())
				.queryParam("key", key).queryParam("sign", sign)
				.queryParam("expiration", expiration).build().toUri();
		return HttpRequestEntity.post(uri)
				.contentType(MediaType.MULTIPART_FORM_DATA).build();
	}
	
	@Override
	public List<UrlResource> list(String prefix, String nextMarker, int limit)
			throws StorageException {
		//TODO 还在考虑更好的实现
		return Collections.emptyList();
	}
}
