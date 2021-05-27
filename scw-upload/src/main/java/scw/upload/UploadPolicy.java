package scw.upload;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import scw.codec.support.CharsetCodec;
import scw.core.utils.StringUtils;
import scw.data.ResourceStorageService;
import scw.data.StorageException;
import scw.http.HttpRequestEntity;
import scw.http.MediaType;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.uri.UriComponentsBuilder;
import scw.util.Verify;
import scw.web.ServerHttpRequest;

public class UploadPolicy implements Verify {
	private static Logger logger = LoggerFactory.getLogger(UploadPolicy.class);
	private String baseUrl;
	private String controller;
	private String sign;

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

	public boolean checkSign(String key, String expiration, String sign) {
		long time = Long.parseLong(expiration);
		if (System.currentTimeMillis() > time) {
			return false;
		}
		return CharsetCodec.UTF_8.toMD5().toSigner().verify(key + expiration + this.sign, sign);
	}

	public HttpRequestEntity<?> generatePolicy(String key, Date expiration) throws StorageException {
		String sign = getSign(key, expiration);
		URI uri = UriComponentsBuilder.fromUriString(getBaseUrl() + getController()).queryParam("key", key)
				.queryParam("sign", sign).queryParam("expiration", expiration.getTime()).build().toUri();
		return HttpRequestEntity.post(uri).contentType(MediaType.MULTIPART_FORM_DATA).build();
	}

	public boolean upload(ServerHttpRequest request, ResourceStorageService rss) throws StorageException, IOException {
		String key = request.getParameterMap().getFirst("key");
		String expiration = request.getParameterMap().getFirst("expiration");
		String sign = request.getParameterMap().getFirst("sign");
		if(StringUtils.isEmpty(key, expiration, sign)){
			return false;
		}
		
		if (!checkSign(key, expiration, sign)) {
			return false;
		}

		logger.info("upload request " + request);
		rss.put(key, request);
		return true;
	}

	@Override
	public boolean isVerified() {
		return StringUtils.isNotEmpty(baseUrl, controller, sign);
	}
}
