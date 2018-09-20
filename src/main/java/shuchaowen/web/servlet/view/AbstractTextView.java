package shuchaowen.web.servlet.view;

import java.io.IOException;

import shuchaowen.core.http.enums.ContentType;
import shuchaowen.core.util.Logger;
import shuchaowen.web.servlet.View;
import shuchaowen.web.servlet.WebResponse;

public abstract class AbstractTextView implements View{
	public abstract String getResponseText();
	
	public void render(WebResponse response) throws IOException{
		if(response.getContentType() == null){
			response.setContentType(ContentType.TEXT_HTML.getValue());
		}
		
		String responseText = getResponseText();
		if(response.getRequest().isDebug()){
			Logger.debug("RESPONSE["+(System.currentTimeMillis() - response.getRequest().getCreateTime())+"ms]", responseText);
		}
		
		response.write(responseText);
	}
}
