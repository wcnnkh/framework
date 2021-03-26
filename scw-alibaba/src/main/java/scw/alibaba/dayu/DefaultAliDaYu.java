package scw.alibaba.dayu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import scw.codec.support.CharsetCodec;
import scw.codec.support.HmacMD5;
import scw.codec.support.MD5;
import scw.context.result.DataResult;
import scw.context.result.ResultFactory;
import scw.core.Constants;
import scw.core.utils.StringUtils;
import scw.core.utils.XTime;
import scw.http.HttpUtils;
import scw.http.MediaType;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public final class DefaultAliDaYu implements AliDaYu {
	private static Logger logger = LoggerFactory
			.getLogger(DefaultAliDaYu.class);

	private final String host;
	private final String appKey;
	private final String version;
	private final String format;
	private final String sign_method;
	private final String appSecret;
	private final ResultFactory resultFactory;

	public DefaultAliDaYu(String appKey, String appSecret,
			ResultFactory resultFactory) {
		this("http://gw.api.taobao.com/router/rest", appKey, "2.0", "json",
				"md5", appSecret, resultFactory);
	}

	public DefaultAliDaYu(String host, String appKey, String version,
			String format, String sign_method, String appSecret,
			ResultFactory resultFactory) {
		this.host = host;
		this.appKey = appKey;
		this.version = version;
		this.format = format;
		this.sign_method = sign_method;
		this.appSecret = appSecret;
		this.resultFactory = resultFactory;
	}

	public DataResult<String> sendMessage(MessageModel messageModel,
			String sms_param, String toPhones) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("app_key", appKey);
		map.put("v", version);
		map.put("format", format);
		map.put("sign_method", sign_method);

		map.put("timestamp",
				XTime.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
		map.put("sms_free_sign_name", messageModel.getSms_free_sign_name());
		map.put("sms_param", sms_param);
		map.put("sms_template_code", messageModel.getSms_template_code());
		map.put("method", "alibaba.aliqin.fc.sms.num.send");
		map.put("sms_type", "normal");

		map.put("sms_param", sms_param);
		map.put("rec_num", toPhones);
		map.put("sign", getSign(map));
		String content = HttpUtils.getHttpClient().post(String.class, host,
				map, MediaType.APPLICATION_FORM_URLENCODED).getBody();
		logger.info(content);
		return resultFactory.success(content);
	}

	/**
	 * 签名
	 * 
	 * @param map
	 * @param secret
	 * @param sign_method
	 * @return
	 */
	protected String getSign(Map<String, String> map) {
		String[] keys = map.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		StringBuilder sb = new StringBuilder();
		boolean isMd5 = false;
		if ("md5".equals(sign_method)) {
			sb.append(appSecret);
			isMd5 = true;
		}

		for (String key : keys) {
			String value = map.get(key);
			if (StringUtils.isEmpty(key, value)) {
				continue;
			}
			// sb.append(key).append(Http.encode(value, "utf-8"));
			sb.append(key).append(value);
		}

		String bytes = null;
		CharsetCodec charsetCodec = new CharsetCodec(Constants.UTF_8_NAME);
		if (isMd5) {
			sb.append(appSecret);
			bytes = MD5.DEFAULT.fromEncoder(charsetCodec).encode(sb.toString());
		} else {
			bytes = new HmacMD5(charsetCodec.encode(appSecret)).toHex().fromEncoder(charsetCodec).encode(sb.toString());
		}

		return bytes.toUpperCase();
	}
}
