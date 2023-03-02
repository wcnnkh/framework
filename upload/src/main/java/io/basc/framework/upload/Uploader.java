package io.basc.framework.upload;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.basc.framework.codec.support.CharsetCodec;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.context.ioc.annotation.Value;
import io.basc.framework.data.DataException;
import io.basc.framework.data.resource.ResourceStorageService;
import io.basc.framework.data.resource.ResourceUploadPolicy;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpRequestEntity;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.http.MediaType;
import io.basc.framework.io.FileUtils;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.FileMimeTypeUitls;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.multipart.MultipartMessage;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.net.uri.UriUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Return;
import io.basc.framework.util.Status;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.TimeUtils;
import io.basc.framework.web.HttpService;
import io.basc.framework.web.MultiPartServerHttpRequest;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.cors.Cors;
import io.basc.framework.web.pattern.ServerHttpRequestAccept;

/**
 * 上传器
 * 
 * @author wcnnkh
 *
 */
@Provider
public class Uploader implements ResourceStorageService, HttpService, ServerHttpRequestAccept {
	public static final String CONTROLLER = "${upload.controller:/upload}";
	private static Logger logger = LoggerFactory.getLogger(Uploader.class);
	private final String directory;
	private String baseUrl;
	@Value(CONTROLLER)
	private String controller;
	private String sign;

	public Uploader(String directory) {
		this.directory = Assert.secureFilePathArgument(directory, "directory");
	}

	@Override
	public Resource get(String key) throws DataException, IOException {
		String cleanKey = cleanPath(key);
		File file = new File(directory, Assert.secureFilePathArgument(key, "key"));
		StringBuilder sb = new StringBuilder();
		sb.append(getBaseUrl());
		sb.append("/");
		sb.append(cleanKey);
		return new UploadResource(file, UriUtils.toUri(sb.toString()));
	}

	@Override
	public boolean put(String key, InputMessage input) throws DataException, IOException {
		logger.info("put [{}]", key);
		File file = new File(directory, Assert.secureFilePathArgument(key, "key"));
		InputStream is = null;
		try {
			is = input.getInputStream();
			FileUtils.copyInputStreamToFile(is, file);
		} finally {
			IOUtils.close(is);
		}
		return true;
	}

	@Override
	public boolean delete(String key) throws DataException {
		logger.info("delete [{}]", key);
		File file = new File(directory, Assert.secureFilePathArgument(key, "key"));
		return file.delete();
	}

	@Override
	public boolean delete(URI uri) throws DataException {
		logger.info("delete [{}]", uri);
		String str = uri.toString();
		str = cleanPath(str);
		if (!str.startsWith(getBaseUrl())) {
			return false;
		}

		String key = str.substring(getBaseUrl().length());
		return delete(key);
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = cleanPath(baseUrl);
	}

	public String getController() {
		return controller;
	}

	public void setController(String controller) {
		this.controller = cleanPath(controller);
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	protected String cleanPath(String key) {
		return StringUtils.cleanPath(key);
	}

	public String getSign(String key, Date expiration) {
		return CharsetCodec.UTF_8.toMD5().encode(cleanPath(key) + expiration.getTime() + sign);
	}

	public Status checkSign(String key, String expiration, String sign) {
		long time = Long.parseLong(expiration);
		if (logger.isDebugEnabled()) {
			logger.debug("Check sign key={} expiration={} sign={}", key, TimeUtils.format(time, "yyyy-MM-dd HH:mm:ss"),
					sign);
		}

		if (System.currentTimeMillis() > time) {
			return Status.error("签名已过期");
		}
		boolean succes = CharsetCodec.UTF_8.toMD5().verify(key + expiration + this.sign, sign);
		if (succes) {
			return Status.success("上传成功");
		}
		return Status.error("签名错误");
	}

	public Return<String> upload(ServerHttpRequest request) throws DataException, IOException {
		if (!(request instanceof MultiPartServerHttpRequest)) {
			return Return.error("无法解析的文件上传请求");
		}

		String key = request.getParameterMap().getFirst("key");
		String expiration = request.getParameterMap().getFirst("expiration");
		String sign = request.getParameterMap().getFirst("sign");
		if (StringUtils.isAnyEmpty(key, expiration, sign)) {
			return Return.error("参数错误");
		}

		Status checkStatus = checkSign(key, expiration, sign);
		if (checkStatus.isError()) {
			return checkStatus.toReturn();
		}

		logger.info("upload request " + request);
		MultiPartServerHttpRequest multiPartServerHttpRequest = (MultiPartServerHttpRequest) request;
		MultipartMessage message = multiPartServerHttpRequest.getFirstFile();
		if (message == null) {
			return Return.error("无文件");
		}
		put(key, message);
		return Return.success(key);
	}

	@Override
	public ResourceUploadPolicy generatePolicy(String key, Date expiration) throws DataException {
		String sign = getSign(key, expiration);
		String baseUrl = cleanPath((StringUtils.isEmpty(getBaseUrl()) ? "" : getBaseUrl()) + getController());
		URI uri = UriComponentsBuilder.fromUriString(baseUrl).queryParam("key", key).queryParam("sign", sign)
				.queryParam("expiration", expiration.getTime()).build().toUri();
		HttpRequestEntity<?> requestEntity = HttpRequestEntity.post(uri).contentType(MediaType.MULTIPART_FORM_DATA)
				.build();
		return new ResourceUploadPolicy(StringUtils.cleanPath(baseUrl + "/" + key), requestEntity);
	}

	@Override
	public boolean test(ServerHttpRequest request) {
		return (request.getMethod() == HttpMethod.GET || request.getMethod() == HttpMethod.POST)
				&& request.getPath().startsWith(getController());
	}

	@Override
	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		if (request.getMethod() == HttpMethod.GET) {
			String key = request.getPath().substring(getController().length());
			Resource resource = get(key);
			MimeType mimeType = FileMimeTypeUitls.getMimeType(resource);
			WebUtils.writeStaticResource(request, response, resource, mimeType);
		} else {
			Return<String> status = upload(request);
			if (status.isSuccess()) {
				Cors.DEFAULT.write(request, response.getHeaders());
				response.setStatusCode(HttpStatus.OK);
			} else {
				response.sendError(HttpStatus.NOT_ACCEPTABLE.value(), status.get());
			}
		}
	}

	@Override
	public List<Resource> list(String keyPrefix, String marker, int limit) throws DataException, IOException {
		String prefix = StringUtils.isEmpty(keyPrefix) ? "" : cleanPath(keyPrefix);
		File file;
		if (StringUtils.isEmpty(prefix)) {
			file = new File(directory);
		} else {
			String suffix = prefix;
			file = new File(directory + Assert.secureFilePath(suffix));
			while (!file.exists() || !file.isDirectory()) {// 如果文件不存在或文件不是目录
				int index = suffix.lastIndexOf("/");
				if (index == -1 || index == 0) {
					break;
				}

				suffix = suffix.substring(0, index);
				file = new File(directory + Assert.secureFilePath(suffix));
			}
		}

		FileFilter fileFilter = new ListFileFilter(keyPrefix, marker, limit);
		List<Resource> list = new ArrayList<Resource>(limit);
		appendFile(fileFilter, file, list);
		return list;
	}

	private final class ListFileFilter implements FileFilter {
		private String keyPrefix;
		private String marker;
		private boolean isFirst;
		private int maxSize;
		private int size = 0;

		public ListFileFilter(String keyPrefix, String marker, int maxSize) {
			this.keyPrefix = keyPrefix;
			this.marker = marker;
			this.isFirst = StringUtils.isEmpty(marker);
			this.maxSize = maxSize;
		}

		public boolean accept(File file) {
			if (!file.isFile()) {
				return false;
			}

			if (size > maxSize) {
				return false;
			}

			String key = getKey(file);
			if (StringUtils.isNotEmpty(marker) && key.equals(marker)) {
				isFirst = true;
			}

			if (!isFirst) {
				return false;
			}

			if (StringUtils.isEmpty(keyPrefix) || key.startsWith(keyPrefix)) {
				size++;
				return true;
			}

			return false;
		}
	}

	private String getKey(File file) {
		String key = file.getPath();
		key = cleanPath(key);
		Assert.isTrue(key.startsWith(directory));
		key = key.substring(directory.length());
		return key.startsWith("/") ? key.substring(1) : key;
	}

	private void appendFile(FileFilter fileFilter, File directory, List<Resource> list)
			throws DataException, IOException {
		for (File fileToUse : directory.listFiles()) {
			if (fileToUse.isDirectory()) {
				appendFile(fileFilter, directory, list);
				continue;
			}

			if (fileFilter.accept(fileToUse)) {
				String key = getKey(fileToUse);
				list.add(get(key));
			}
		}
	}
}
