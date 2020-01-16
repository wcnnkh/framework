package scw.alibaba.dayu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import scw.core.utils.StringUtils;
import scw.core.utils.XTime;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.http.HttpUtils;
import scw.result.DataResult;
import scw.result.ResultFactory;
import scw.security.signature.SignatureUtils;

public final class DefaultAliDaYu implements AliDaYu {
	private static Logger logger = LoggerFactory.getLogger(DefaultAliDaYu.class);

	private final String host;
	private final String appKey;
	private final String version;
	private final String format;
	private final String sign_method;
	private final String appSecret;
	private final ResultFactory resultFactory;

	public DefaultAliDaYu(String appKey, String appSecret, ResultFactory resultFactory) {
		this("http://gw.api.taobao.com/router/rest", appKey, "2.0", "json", "md5", appSecret, resultFactory);
	}

	public DefaultAliDaYu(String host, String appKey, String version, String format, String sign_method,
			String appSecret, ResultFactory resultFactory) {
		this.host = host;
		this.appKey = appKey;
		this.version = version;
		this.format = format;
		this.sign_method = sign_method;
		this.appSecret = appSecret;
		this.resultFactory = resultFactory;
	}

	public DataResult<String> sendMessage(MessageModel messageModel, String sms_param, String toPhones) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("app_key", appKey);
		map.put("v", version);
		map.put("format", format);
		map.put("sign_method", sign_method);

		map.put("timestamp", XTime.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
		map.put("sms_free_sign_name", messageModel.getSms_free_sign_name());
		map.put("sms_param", sms_param);
		map.put("sms_template_code", messageModel.getSms_template_code());
		map.put("method", "alibaba.aliqin.fc.sms.num.send");
		map.put("sms_type", "normal");

		map.put("sms_param", sms_param);
		map.put("rec_num", toPhones);
		map.put("sign", getSign(map));
		String content = HttpUtils.getHttpClient().postForFrom(host, map);
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
			if (StringUtils.isNull(key, value)) {
				continue;
			}
			// sb.append(key).append(Http.encode(value, "utf-8"));
			sb.append(key).append(value);
		}

		String bytes = null;
		if (isMd5) {
			sb.append(appSecret);
			bytes = SignatureUtils.md5(sb.toString(), "UTF-8");
		} else {
			bytes = SignatureUtils.hmacMD5(sb.toString(), appSecret, "UTF-8");
		}

		return bytes.toUpperCase();
	}
}
