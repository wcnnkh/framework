package shuchaowen.web.servlet.view;

import java.io.IOException;

import shuchaowen.core.util.Logger;
import shuchaowen.web.servlet.View;
import shuchaowen.web.servlet.WebRequest;
import shuchaowen.web.servlet.WebResponse;

public class Redirect implements View{
	private String url;
	
	public Redirect(String url){
		this.url = url;
	}
	
	public void render(WebRequest request, WebResponse response
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
