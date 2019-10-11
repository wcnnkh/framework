package scw.security.session.http;

import scw.core.annotation.Order;
import scw.core.annotation.ParameterName;
import scw.core.annotation.ParameterValue;
import scw.core.utils.StringUtils;
import scw.mvc.FilterChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpFilter;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.result.ResultFactory;
import scw.security.session.UserSession;

@SuppressWarnings("rawtypes")
public class HttpUserSessionFilter extends HttpFilter{
	private HttpChannelUserSessionFactory httpChannelUserSessionFactory;
	private String uidKey;
	private ResultFactory resultFactory;
	
	@Order
	public HttpUserSessionFilter(HttpChannelUserSessionFactory httpChannelUserSessionFactory, @ParameterName("http.user.session.filter.uid.key")@ParameterValue("uid")String uidKey, ResultFactory resultFactory){
		this.httpChannelUserSessionFactory = httpChannelUserSessionFactory;
		this.uidKey = uidKey;
		this.resultFactory = resultFactory;
	}
	
	@Override
	public Object doFilter(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse, FilterChain chain)
			throws Throwable {
		UserSession userSession = httpChannelUserSessionFactory.getUserSession(channel);
		if(userSession == null){
			return resultFactory.authorizationFailure();
		}

		if(!StringUtils.isEmpty(uidKey)){
			String uid = channel.getString(uidKey);
			if(uid == null){
				return resultFactory.authorizationFailure();
			}
			
			Object cacheUid = userSession.getUid();
			if(cacheUid == null){
				return resultFactory.authorizationFailure();
			}

			if(!uid.equals(cacheUid.toString())){
				return resultFactory.authorizationFailure();
			}
		}
		
		return chain.doFilter(channel);
	}

}
