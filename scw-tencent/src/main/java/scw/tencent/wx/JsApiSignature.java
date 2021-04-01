package scw.tencent.wx;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import scw.codec.encoder.SHA1;
import scw.codec.support.CharsetCodec;
import scw.core.Assert;
import scw.core.utils.StringUtils;
import scw.util.RandomUtils;

public final class JsApiSignature implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String nonceStr;// 注意 这个随机字符串的S在前端是大写的，可是在签名的时候是小写的
	private final int timestamp;// 单位：秒
	private final String url;
	private final String signature;

	public JsApiSignature(String jsapi_ticket, String url) {
		this(RandomUtils.getRandomStr(10), jsapi_ticket, (int) (System
				.currentTimeMillis() / 1000), url);
	}

	public JsApiSignature(String nonceStr, String jsapi_ticket, int timestamp,
			String url) {
		Assert.isTrue(StringUtils.isNotEmpty(nonceStr, jsapi_ticket, url));
		this.nonceStr = nonceStr;
		this.timestamp = timestamp;
		this.url = url;

		Map<String, String> map = new HashMap<String, String>();
		map.put("noncestr", nonceStr);
		map.put("timestamp", timestamp + "");
		map.put("url", url);
		map.put("jsapi_ticket", jsapi_ticket);

		String[] keys = map.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			sb.append(key).append("=").append(map.get(key));
			if (i < keys.length - 1) {
				sb.append("&");
			}
		}
		
		this.signature = CharsetCodec.UTF_8.toEncoder(SHA1.DEFAULT).encode(sb.toString());
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public String getUrl() {
		return url;
	}

	public String getSignature() {
		return signature;
	}
}
