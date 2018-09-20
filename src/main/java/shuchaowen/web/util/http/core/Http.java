package shuchaowen.web.util.http.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.web.util.http.impl.HttpCallBack;
import shuchaowen.web.util.http.impl.HttpOutput;
import shuchaowen.web.util.http.impl.HttpSetting;

public class Http {
	public static final String default_encode = "UTF-8";
	public static final int default_connectTimeout = 5000;
	public static final int default_readTimeout = 5000;
	public static final String BOUNDARY = "----WebKitFormBoundaryKSD2ndz6G9RPNjx0";
	public static final String BOUNDARY_TAG = "--";
	public static final String BR = "\r\n";
	
	public final static boolean invoke(String url, HttpSetting setting, HttpOutput output, HttpCallBack callback){
		URL connUrl = null;
		HttpURLConnection conn = null;
		OutputStream os = null;
		try {
			connUrl = new URL(url);
			conn = (HttpURLConnection) connUrl.openConnection();
			if(setting != null){
				setting.setting(conn);
			}
			
			if(output != null){
				os = conn.getOutputStream();
				output.output(os);
				os.flush();
				os.close();
			}
			
			if(callback != null){
				callback.call(conn);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(conn != null){
				try {
					conn.disconnect();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return false;
	}
	
	public final static void setRequestPropertys(HttpURLConnection conn, Map<String, String> requestPropertys){
		if(requestPropertys != null){
			for(Entry<String, String> entry : requestPropertys.entrySet()){
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public final static void appendStr(InputStream is, StringBuilder sb, String encode) throws IOException{
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(
				is, encode));
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
	}
	
	public static final String encode(String str){
		return encode(str, default_encode);
	}
	
	public static final String decode(String str){
		return decode(str, default_encode);
	}
	
	public static final String encode(String str, String encode){
		try {
			return URLEncoder.encode(str, encode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final String decode(String str, String encode){
		try {
			return URLDecoder.decode(str, encode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public final static String getParams(Map<String, String> paramMap){
		return getParams(null, paramMap);
	}
	
	public final static String getParams(String profix, Map<String, String> paramMap){
		return getParams(profix, paramMap, default_encode);
	}
	
	/**
	 * 组装参数
	 * @param profix  在参数前面追加
	 * @param paramMap
	 * @param encode 有参数时行编码 为空则不时行编码
	 * @return
	 */
	public final static String getParams(String profix, Map<String, String> paramMap, String encode){
		StringBuilder sb = new StringBuilder();
		if(paramMap != null){
			Iterator<Entry<String, String>> iterator = paramMap.entrySet().iterator();
			if(iterator.hasNext() && profix != null){
				sb.append(profix);
			}
			
			while(iterator.hasNext()){
				Entry<String, String> entry = iterator.next();
				if(entry.getValue() == null){
					continue;
				}
				
				sb.append(entry.getKey());
				sb.append("=");
				if(encode == null){
					sb.append(entry.getValue());
				}else{
					sb.append(encode(entry.getValue(), encode));
				}
				if(iterator.hasNext()){
					sb.append("&");
				}
			}
		}
		return sb.toString();
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
