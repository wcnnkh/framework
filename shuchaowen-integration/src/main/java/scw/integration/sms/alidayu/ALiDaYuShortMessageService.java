package scw.integration.sms.alidayu;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import scw.core.utils.StringUtils;
import scw.core.utils.XTime;
import scw.http.HttpUtils;
import scw.http.MediaType;
import scw.integration.sms.ShortMessageService;
import scw.integration.sms.SmsException;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.security.SignatureUtils;
import scw.util.phone.PhoneNumber;

public final class ALiDaYuShortMessageService implements
		ShortMessageService<ALiDaYuMessage, ALiDaYuResult> {
	private static Logger logger = LoggerFactory
			.getLogger(ALiDaYuShortMessageService.class);
	private final String host;
	private final String appKey;
	private final String version;
	private final String format;
	private final String sign_method;
	private final String appSecret;

	public ALiDaYuShortMessageService(String host, String appKey,
			String version, String format, String sign_method, String appSecret) {
		this.host = host;
		this.appKey = appKey;
		this.version = version;
		this.format = format;
		this.sign_method = sign_method;
		this.appSecret = appSecret;
	}

	public ALiDaYuResult send(ALiDaYuMessage message, PhoneNumber phoneNumber)
			throws SmsException {
		return send(message, Arrays.asList(phoneNumber));
	}

	public ALiDaYuResult send(ALiDaYuMessage message,
			Collection<PhoneNumber> phoneNumbers) throws SmsException {
		return send(message, joinPhoneNumber(phoneNumbers));
	}

	private String joinPhoneNumber(Collection<PhoneNumber> phoneNumbers) {
		StringBuilder sb = new StringBuilder();
		Iterator<PhoneNumber> iterator = phoneNumbers.iterator();
		while (iterator.hasNext()) {
			PhoneNumber phoneNumber = iterator.next();
			if (phoneNumber == null) {
				continue;
			}

			if (sb.length() != 0) {
				sb.append(",");
			}
			sb.append(phoneNumber.getNumber());
		}
		return sb.toString();
	}

	public ALiDaYuResult send(ALiDaYuMessage message, String phones) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("app_key", appKey);
		map.put("v", version);
		map.put("format", format);
		map.put("sign_method", sign_method);

		map.put("timestamp",
				XTime.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
		map.put("sms_free_sign_name", message.getMessageModel()
				.getSms_free_sign_name());
		map.put("sms_param", JSONUtils.toJSONString(message.getSms_param()));
		map.put("sms_template_code", message.getMessageModel()
				.getSms_template_code());
		map.put("method", "alibaba.aliqin.fc.sms.num.send");
		map.put("sms_type", "normal");

		map.put("rec_num", phones);
		map.put("sign", getSign(map));
		String content = HttpUtils.getHttpClient().post(host, String.class,
				map, MediaType.APPLICATION_FORM_URLENCODED);
		logger.debug(content);
		if (content == null) {
			return null;
		}
		return new ALiDaYuResult(content);
	}

	/**
	 * 签名
	 * 
	 * @param map
	 * @param secret
	 * @param sign_method
	 * @return
	 */
	private String getSign(Map<String, String> map) {
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
