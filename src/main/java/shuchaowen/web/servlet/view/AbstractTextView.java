package shuchaowen.web.servlet.view;

import java.io.IOException;

import shuchaowen.core.connection.http.enums.ContentType;
import shuchaowen.web.servlet.View;
import shuchaowen.web.servlet.Request;
import shuchaowen.web.servlet.Response;

public abstract class AbstractTextView implements View{
	public abstract String getResponseText();
	
	public void render(Request request, Response response) throws IOException{
		if(response.getContentType() == null){
			response.setContentType(ContentType.TEXT_HTML.getValue());
		}
		
		response.write(getResponseText());
	}
}
