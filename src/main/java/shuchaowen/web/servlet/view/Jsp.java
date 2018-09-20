package shuchaowen.web.servlet.view;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.Logger;
import shuchaowen.web.servlet.View;
import shuchaowen.web.servlet.WebRequest;
import shuchaowen.web.servlet.WebResponse;

public class Jsp extends HashMap<String, Object> implements View{
	private static final long serialVersionUID = 1L;
	private String page;
	
	public Jsp(String page) {
		this.page = page;
	}
	
	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public void render(WebResponse response) throws IOException{
		if(response.getContentType() == null){
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}
		
		if(getPage() == null){
			setPage(response.getRequest().getServletPath());
		}
		
		for(Entry<String, Object> entry : entrySet()){
			response.getRequest().setAttribute(entry.getKey(), entry.getValue());
		}

		String page = getPage();
		if(page == null){
			page = response.getRequest().getServletPath() + ".jsp";
		}
		
		try {
			jsp(response.getRequest(), response, getPage());
		} catch (ServletException e) {
			throw new ShuChaoWenRuntimeException(e);
		}finally{
			if(response.getRequest().isDebug()){
				Logger.debug("RESPONSE[" + (System.currentTimeMillis() - response.getRequest().getCreateTime())+ "ms]", "jsp:" + page);
			}
		}
	}
	
	public static void jsp(WebRequest request, WebResponse response, String page) throws ServletException, IOException{
		RequestDispatcher dispatcher = request.getRequestDispatcher(page);
		dispatcher.forward(request, response);
	}
}
