package shuchaowen.web.servlet.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.common.enums.ContentType;
import shuchaowen.web.servlet.Request;
import shuchaowen.web.servlet.Response;
import shuchaowen.web.servlet.View;

public abstract class AbstractTextView implements View{
	private Map<String, String> responseProperties;
	
	public abstract String getResponseText();
	
	public void addResponseHeader(String key, String value){
		if(responseProperties == null){
			responseProperties = new HashMap<String, String>(8);
		}
		responseProperties.put(key, value);
	}
	
	public void render(Request request, Response response) throws IOException{
		if(responseProperties != null){
			for(Entry<String, String> entry : responseProperties.entrySet()){
				response.setHeader(entry.getKey(), entry.getValue());
			}
		}
		
		if(response.getContentType() == null){
			response.setContentType(ContentType.TEXT_HTML.getValue());
		}
		response.write(getResponseText());
	}
}
