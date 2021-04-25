package scw.upload;

import scw.codec.support.CharsetCodec;
import scw.core.utils.StringUtils;
import scw.util.Verify;

public class UploadPolicy implements Verify{
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

	public String getSign(String key, long expiration){
		return CharsetCodec.UTF_8.toMD5().encode(key + expiration + sign);
	}
	
	public boolean check(String key, String expiration, String sign){
		long time = Long.parseLong(expiration);
		if(System.currentTimeMillis() > time){
			return false;
		}
		return CharsetCodec.UTF_8.toMD5().toSigner().verify(key + expiration + this.sign, sign);
	}

	@Override
	public boolean isVerified() {
		return StringUtils.isNotEmpty(baseUrl, controller, sign);
	}
}
