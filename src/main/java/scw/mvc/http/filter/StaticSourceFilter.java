package scw.mvc.http.filter;

import java.io.File;

import scw.mvc.FilterChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpFilter;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;

//TODO 未完成
public class StaticSourceFilter extends HttpFilter{
	private final String filePath;
	
	public StaticSourceFilter(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public Object doFilter(HttpChannel channel, HttpRequest httpRequest,
			HttpResponse httpResponse, FilterChain chain) throws Throwable {
		File file = new File(filePath + httpRequest.getRequestPath());
		if(!file.exists() || !file.isFile()){
			return chain.doFilter(channel);
		}
		
		return null;//TODO 
	}

}
