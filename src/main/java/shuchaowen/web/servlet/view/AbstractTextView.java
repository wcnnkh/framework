package shuchaowen.web.servlet.view;

import java.io.IOException;

import shuchaowen.core.http.enums.ContentType;
import shuchaowen.core.util.Logger;
import shuchaowen.web.servlet.View;
import shuchaowen.web.servlet.WebRequest;
import shuchaowen.web.servlet.WebResponse;

public abstract class AbstractTextView implements View{
	public abstract String getResponseText();
	
	public void render(WebRequest request, WebResponse response) throws IOException{
		if(response.getContentType() == null){
			response.setContentType(ContentType.TEXT_HTML.getValue());
		}
		
		String responseText = getResponseText();
		if(response.getRequest().isDebug()){
			Logger.debug("RESPONSE["+(System.currentTimeMillis() - request.getCreateTime())+"ms]", responseText);
		}
		
		response.write(responseText);
	}
}
