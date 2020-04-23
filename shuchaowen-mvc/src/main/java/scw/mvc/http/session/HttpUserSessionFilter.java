package scw.mvc.http.session;

import scw.core.annotation.Order;
import scw.core.parameter.annotation.DefaultValue;
import scw.core.parameter.annotation.ParameterName;
import scw.core.utils.StringUtils;
import scw.mvc.action.filter.ActionFilterChain;
import scw.mvc.action.manager.HttpAction;
import scw.mvc.http.HttpChannel;
import scw.result.ResultFactory;
import scw.security.session.UserSession;

@SuppressWarnings("rawtypes")
public class HttpUserSessionFilter extends scw.mvc.action.filter.HttpActionFilter	{
	private HttpChannelUserSessionFactory httpChannelUserSessionFactory;
	private String uidKey;
	private ResultFactory resultFactory;
	
	@Order
	public HttpUserSessionFilter(HttpChannelUserSessionFactory httpChannelUserSessionFactory, @ParameterName("http.user.session.filter.uid.key")@DefaultValue("uid")String uidKey, ResultFactory resultFactory){
		this.httpChannelUserSessionFactory = httpChannelUserSessionFactory;
		this.uidKey = uidKey;
		this.resultFactory = resultFactory;
	}
	
	@Override
	protected Object doHttpFilter(HttpChannel channel, HttpAction action,
			ActionFilterChain chain) throws Throwable {
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
		
		return chain.doFilter(channel, action);
	}

}
