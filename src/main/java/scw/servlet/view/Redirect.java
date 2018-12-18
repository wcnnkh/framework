package scw.servlet.view;

import java.io.IOException;

import scw.common.Logger;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.View;

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
