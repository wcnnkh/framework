package scw.upload;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import scw.beans.annotation.Value;
import scw.codec.support.CharsetCodec;
import scw.context.annotation.Provider;
import scw.core.Assert;
import scw.core.utils.StringUtils;
import scw.data.ResourceStorageService;
import scw.data.StorageException;
import scw.http.HttpMethod;
import scw.http.HttpRequestEntity;
import scw.http.HttpStatus;
import scw.http.MediaType;
import scw.io.FileUtils;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.FileMimeTypeUitls;
import scw.net.MimeType;
import scw.net.message.InputMessage;
import scw.net.message.multipart.MultipartMessage;
import scw.net.uri.UriComponentsBuilder;
import scw.net.uri.UriUtils;
import scw.util.DefaultStatus;
import scw.util.Status;
import scw.web.HttpService;
import scw.web.MultiPartServerHttpRequest;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.WebUtils;
import scw.web.cors.Cors;
import scw.web.pattern.ServerHttpRequestAccept;

/**
 * 上传器
 * 
 * @author shuchaowen
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
		this.directory = directory;
	}

	@Override
	public Resource get(String key) throws StorageException, IOException {
		File file = new File(directory, key);
		StringBuilder sb = new StringBuilder();
		sb.append(getBaseUrl());
		sb.append("/");
		sb.append(key);
		return new UploadResource(file, UriUtils.toUri(sb.toString()));
	}

	@Override
	public boolean put(String key, InputMessage input) throws StorageException, IOException {
		logger.info("put [{}]", key);
		File file = new File(directory, key);
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
	public boolean delete(String key) throws StorageException {
		logger.info("delete [{}]", key);
		File file = new File(directory, StringUtils.cleanPath(key));
		return file.delete();
	}

	@Override
	public boolean delete(URI uri) throws StorageException {
		logger.info("delete [{}]", uri);
		String str = uri.toString();
		str = StringUtils.cleanPath(str);
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
		this.baseUrl = StringUtils.cleanPath(baseUrl);
	}

	public String getController() {
		return controller;
	}

	public void setController(String controller) {
		this.controller = StringUtils.cleanPath(controller);
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSign(String key, Date expiration) {
		return CharsetCodec.UTF_8.toMD5().encode(key + expiration.getTime() + sign);
	}

	public Status<String> checkSign(String key, String expiration, String sign) {
		long time = Long.parseLong(expiration);
		if (System.currentTimeMillis() > time) {
			return new DefaultStatus<>(false, "签名已过期");
		}
		boolean succes = CharsetCodec.UTF_8.toMD5().toSigner().verify(key + expiration + this.sign, sign);
		if (succes) {
			return new DefaultStatus<>(true, "上传成功");
		}
		return new DefaultStatus<>(false, "签名错误");
	}

	public Status<String> upload(ServerHttpRequest request) throws StorageException, IOException {
		if(!(request instanceof MultiPartServerHttpRequest)){
			return new DefaultStatus<String>(false, "无法解析的文件上传请求");
		}
		
		String key = request.getParameterMap().getFirst("key");
		String expiration = request.getParameterMap().getFirst("expiration");
		String sign = request.getParameterMap().getFirst("sign");
		if (StringUtils.isEmpty(key, expiration, sign)) {
			return new DefaultStatus<>(false, "参数错误");
		}

		Status<String> checkStatus = checkSign(key, expiration, sign);
		if (!checkStatus.isActive()) {
			return checkStatus;
		}

		logger.info("upload request " + request);
		MultiPartServerHttpRequest multiPartServerHttpRequest = (MultiPartServerHttpRequest) request;
		MultipartMessage message = multiPartServerHttpRequest.getFirstFile();
		if(message == null){
			return new DefaultStatus<String>(false, "无文件");
		}
		put(key, message);
		return new DefaultStatus<>(true, key);
	}

	@Override
	public UploadPolicy generatePolicy(String key, Date expiration) throws StorageException {
		String sign = getSign(key, expiration);
		String baseUrl = StringUtils
				.cleanPath((StringUtils.isEmpty(getBaseUrl()) ? "" : getBaseUrl()) + getController());
		URI uri = UriComponentsBuilder.fromUriString(baseUrl).queryParam("key", key).queryParam("sign", sign)
				.queryParam("expiration", expiration.getTime()).build().toUri();
		HttpRequestEntity<?> requestEntity = HttpRequestEntity.post(uri).contentType(MediaType.MULTIPART_FORM_DATA)
				.build();
		return new UploadPolicy(StringUtils.cleanPath(baseUrl + "/" + key), requestEntity);
	}

	@Override
	public boolean accept(ServerHttpRequest request) {
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
			Status<String> status = upload(request);
			if (status.isActive()) {
				Cors.DEFAULT.write(request, response.getHeaders());
				response.setStatusCode(HttpStatus.OK);
			} else {
				response.setStatusCode(HttpStatus.FORBIDDEN);
			}
		}
	}

	@Override
	public List<Resource> list(String keyPrefix, String marker, int limit) throws StorageException, IOException {
		String prefix = StringUtils.isEmpty(keyPrefix) ? "" : StringUtils.cleanPath(keyPrefix);
		File file;
		if (StringUtils.isEmpty(prefix)) {
			file = new File(directory);
		} else {
			String suffix = prefix;
			file = new File(directory + suffix);
			while (!file.exists() || !file.isDirectory()) {// 如果文件不存在或文件不是目录
				int index = suffix.lastIndexOf("/");
				if (index == -1 || index == 0) {
					break;
				}

				suffix = suffix.substring(0, index);
				file = new File(directory + suffix);
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
		key = StringUtils.cleanPath(key);
		Assert.isTrue(key.startsWith(directory));
		key = key.substring(directory.length());
		return key.startsWith("/") ? key.substring(1) : key;
	}

	private void appendFile(FileFilter fileFilter, File directory, List<Resource> list)
			throws StorageException, IOException {
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
