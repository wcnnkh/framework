package scw.utils.ali.dayu;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import scw.common.ProcessResult;
import scw.common.utils.SignUtils;
import scw.common.utils.StringUtils;
import scw.common.utils.XTime;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.http.HttpUtils;

public final class DefaultAliDaYu implements AliDaYu {
	private static Logger logger = LoggerFactory.getLogger(DefaultAliDaYu.class);

	private String host;
	private String appKey;
	private String version;
	private String format;
	private String sign_method;
	private String appSecret;

	public DefaultAliDaYu(String appKey, String appSecret) {
		this("http://gw.api.taobao.com/router/rest", appKey, "2.0", "json", "md5", appSecret);
	}

	public DefaultAliDaYu(String host, String appKey, String version, String format, String sign_method,
			String appSecret) {
		this.host = host;
		this.appKey = appKey;
		this.version = version;
		this.format = format;
		this.sign_method = sign_method;
		this.appSecret = appSecret;
	}

	public ProcessResult<String> sendMessage(MessageModel messageModel, String sms_param, String toPhones) {
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
		String content = HttpUtils.doPost(host, null, map);
		;
		logger.debug(content);
		return ProcessResult.success(content);
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

		byte[] bytes = null;
		if (isMd5) {
			sb.append(appSecret);
			try {
				bytes = SignUtils.md5(sb.toString().getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		} else {
			bytes = SignUtils.HmacMD5(sb.toString(), appSecret, "UTF-8");
		}

		return SignUtils.byte2hex(bytes).toUpperCase();
	}
}
