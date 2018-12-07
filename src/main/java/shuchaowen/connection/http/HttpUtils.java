package shuchaowen.connection.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.common.exception.ShuChaoWenRuntimeException;
import shuchaowen.common.utils.StringUtils;
import shuchaowen.connection.http.write.FormData;

public final class HttpUtils {
	private static final String DEFAULT_CHARSETNAME = "UTF-8";
	
	private HttpUtils(){};
	
	public static String doGet(String url){
		return doGet(url, DEFAULT_CHARSETNAME);
	}
	
	public static String doGet(String url, String charsetName){
		HttpGET http = null;
		try {
			http = new HttpGET(url);
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + charsetName);
			return http.getBody(charsetName);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(http != null){
				http.disconnect();
			}
		}
		return null;
	}
	
	public static byte[] doPost(String url, Map<String, String> propertyMap, byte[] data){
		HttpPOST http = null;
		try {
			http = new HttpPOST(url);
			http.setRequestProperty("Content-Type", "application/octet-stream");
			if(propertyMap != null && !propertyMap.isEmpty()){
				for(Entry<String, String> entry : propertyMap.entrySet()){
					http.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			return http.getResponse().toByteArray();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(http != null){
				http.disconnect();
			}
		}
		return null;
	}
	
	public static String doPost(String url, Map<String, String> propertyMap, String body, String charsetName){
		HttpPOST http = null;
		try {
			http = new HttpPOST(url);
			http.setRequestProperty("Content-Type", "application/octet-stream");
			if(propertyMap != null && !propertyMap.isEmpty()){
				for(Entry<String, String> entry : propertyMap.entrySet()){
					http.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			
			if(body != null){
				http.getOutputStream().write(body.getBytes(charsetName));
			}
			return http.getBody(charsetName);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(http != null){
				http.disconnect();
			}
		}
		return null;
	}
	
	public static String doPost(String url, Map<String, String> propertyMap, String body){
		return doPost(url, propertyMap, body, DEFAULT_CHARSETNAME);
	}
	
	public static String doPost(String url, Map<String, String> propertyMap, Map<String, ?> parameterMap, String charsetName){
		HttpPOST http = null;
		try {
			http = new HttpPOST(url);
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + charsetName);
			if(propertyMap != null && !propertyMap.isEmpty()){
				for(Entry<String, String> entry : propertyMap.entrySet()){
					http.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			http.write(FormData.wrapper(parameterMap, charsetName));
			return http.getBody(charsetName);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(http != null){
				http.disconnect();
			}
		}
		return null;
	}
	
	public static String doPost(String url, Map<String, ?> parameterMap){
		return doPost(url, null, parameterMap, DEFAULT_CHARSETNAME);
	}
	
	public static String appendParameters(String prefix, Map<String, Object> paramMap, boolean encode, String charsetName) throws UnsupportedEncodingException{
		if(prefix == null || paramMap == null || paramMap.isEmpty()){
			return prefix;
		}
		
		StringBuilder sb = new StringBuilder(128);
		sb.append(prefix);
		if(prefix != null || prefix.lastIndexOf("?") == -1){
			sb.append("?");
		}
	
		Iterator<Entry<String, Object>> iterator = paramMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Object> entry = iterator.next();
			if(StringUtils.isNull(entry.getKey()) || entry.getValue() == null){
				continue;
			}
			
			sb.append(entry.getKey());
			sb.append("=");
			if(encode){
				sb.append(URLEncoder.encode(entry.getValue().toString(), charsetName));
			}else{
				sb.append(entry.getValue());
			}
		}
		return sb.toString();
	}
	
	public static String appendParameters(String prefix, Map<String, Object> paramMap){
		try {
			return appendParameters(prefix, paramMap, true, DEFAULT_CHARSETNAME);
		} catch (UnsupportedEncodingException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
	
	public static String encode(Object value, String charsetName) throws UnsupportedEncodingException{
		if(value == null){
			return null;
		}
		
		return URLEncoder.encode(value.toString(), charsetName);
	}
	
	public static String encode(Object value){
		try {
			return encode(value, DEFAULT_CHARSETNAME);
		} catch (UnsupportedEncodingException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
	
	public static String decode(String value, String charsetName) throws UnsupportedEncodingException{
		if(value == null){
			return null;
		}
		
		return URLDecoder.decode(value, charsetName);
	}
	
	public static String decode(String value){
		try {
			return decode(value, DEFAULT_CHARSETNAME);
		} catch (UnsupportedEncodingException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
	
	public static String decode(String content, String charsetName, int count) throws UnsupportedEncodingException{
		if(count <= 0){
			return content;
		}
		
		String newContent = content;
		for(int i=0; i<count; i++){
			newContent = decode(newContent, charsetName);
		}
		return newContent;
	}
	
	public static String encode(String content, String charsetName, int count) throws UnsupportedEncodingException{
		if(count <= 0){
			return content;
		}
		
		String newContent = content;
		for(int i=0; i<count; i++){
			newContent = encode(newContent, charsetName);
		}
		return newContent;
	}
	
	public static final Map<String, String> paramsToMap(String params){
		Map<String, String> map = new Hashtable<String, String>();
		if(params != null){
			String[] strs = params.split("&");
			for(String str : strs){
				String[] temp = str.split("=");
				if(temp.length == 2){
					map.put(temp[0], temp[1]);
				}
			}
		}
		return map;
	}
}
