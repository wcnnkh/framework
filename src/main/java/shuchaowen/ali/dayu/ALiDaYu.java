package shuchaowen.ali.dayu;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.util.SignHelp;
import shuchaowen.core.util.StringUtils;
import shuchaowen.core.util.XTime;
import shuchaowen.web.util.http.HttpPost;

public class ALiDaYu {
	private String host;
	private String appKey;
	private String version;
	private String format;
	private String sign_method;
	private String appSecret;
	
	public ALiDaYu(String appKey, String appSecret) {
		this("http://gw.api.taobao.com/router/rest", appKey, "2.0", "json", "md5", appSecret);
	}
	
	public ALiDaYu(String host, String appKey, String version, String format, String sign_method, String appSecret) {
		this.host = host;
		this.appKey = appKey;
		this.version = version;
		this.format = format;
		this.sign_method = sign_method;
		this.appSecret = appSecret;
	}
	
	public String sendMessage(String sms_param, String sms_free_sign_name, String sms_template_code, String toPhones){
		Map<String, String> map = new HashMap<String, String>();
		map.put("app_key", appKey);
		map.put("v", version);
		map.put("format", format);
		map.put("sign_method", sign_method);
		
		map.put("timestamp", XTime.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
		map.put("sms_free_sign_name", sms_free_sign_name);
		map.put("sms_param", sms_param);
		map.put("sms_template_code", sms_template_code);
		map.put("method", "alibaba.aliqin.fc.sms.num.send");
		map.put("sms_type", "normal");
		
		map.put("sms_param", sms_param);
		map.put("rec_num", toPhones);
		map.put("sign", getSign(map, appSecret, sign_method));
		return HttpPost.invoke(host, map);
	}
	
	
	/**
	 * 签名
	 * @param map
	 * @param secret
	 * @param sign_method
	 * @return
	 */
	public static String getSign(Map<String, String> map, String secret, String sign_method){
		String[] keys = map.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		StringBuilder sb = new StringBuilder();
		boolean isMd5 = false;
		if("md5".equals(sign_method)){
			sb.append(secret);
			isMd5 = true;
		}
		
		for(String key : keys){
			String value = map.get(key);
			if(StringUtils.isNull(key, value)){
				continue;
			}
			//sb.append(key).append(Http.encode(value, "utf-8"));
			sb.append(key).append(value);
		}
				
		byte[] bytes = null;
		if(isMd5){
			sb.append(secret);
			try {
				bytes = SignHelp.md5(sb.toString().getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}else{
			bytes = SignHelp.HmacMD5(sb.toString(), secret, "UTF-8");
		}
		
		return SignHelp.byte2hex(bytes).toUpperCase();
	}
}
