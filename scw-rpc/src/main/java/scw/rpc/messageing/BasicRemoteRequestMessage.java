package scw.rpc.messageing;

import java.util.Enumeration;

import scw.lang.NamedThreadLocal;
import scw.util.attribute.SimpleAttributes;

public class BasicRemoteRequestMessage extends SimpleAttributes<String, Object> implements RemoteRequestMessage{
	private static final long serialVersionUID = 1L;
	private static ThreadLocal<MessageHeaders> HEADERS_LOCAL = new NamedThreadLocal<MessageHeaders>("REMOTE_REQUEST_MESSAGE_HEADERS");
	
	public static void setLocalHeaders(MessageHeaders headers){
		if(headers == null){
			HEADERS_LOCAL.remove();
		}else{
			HEADERS_LOCAL.set(headers);
		}
	}
	
	public BasicRemoteRequestMessage(){
		MessageHeaders headers = HEADERS_LOCAL.get();
		if(headers != null){
			Enumeration<String> keys = headers.getAttributeNames();
			while(keys.hasMoreElements()){
				String key = keys.nextElement();
				setAttribute(key, headers.getAttribute(key));
			}
		}
	}
}
