package shuchaowen.web.servlet.view;

import java.io.IOException;

import shuchaowen.web.servlet.View;
import shuchaowen.common.Logger;
import shuchaowen.web.servlet.Request;
import shuchaowen.web.servlet.Response;

public class Redirect implements View{
	private String url;
	
	public Redirect(String url){
		this.url = url;
	}
	
	public void render(Request request, Response response
			) throws IOException{
		if(response.getContentType() == null){
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}
			
		if(response.getRequest().isDebug()){
			Logger.debug(this.getClass().getName(), url);
		}
		
		response.sendRedirect(url);
	}
}
