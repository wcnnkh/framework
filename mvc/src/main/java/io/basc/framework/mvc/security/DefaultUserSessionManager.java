package io.basc.framework.mvc.security;

import java.net.HttpCookie;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.core.convert.ValueWrapper;
import io.basc.framework.core.env.Sys;
import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.security.login.UserToken;
import io.basc.framework.security.session.UserSession;
import io.basc.framework.security.session.UserSessionFactory;
import io.basc.framework.security.session.UserSessions;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.web.WebUtils;

@ConditionalOnParameters
public class DefaultUserSessionManager implements UserSessionManager {
	private static final String UID_ATTRIBUTE = "io.basc.framework.mvc.http.channel.uid";
	private static final String SESSIONID_ATTRIBUTE = "io.basc.framework.mvc.http.channel.sessionid";
	private static final String SINGLE_ATTRIBUTE = "io.basc.framework.mvc.http.channel.single.session";
	private static final String TOKEN_NAME = Sys.getEnv().getProperties().get(SESSIONID_ATTRIBUTE).or("token")
			.getAsString();
	private static final String UID_NAME = Sys.getEnv().getProperties().get(UID_ATTRIBUTE).or("uid").getAsString();
	private static final boolean SINGLE_SESSION = Sys.getEnv().getProperties().getAsBoolean(SINGLE_ATTRIBUTE);

	private static Logger logger = LogManager.getLogger(DefaultUserSessionManager.class);

	private final UserSessionFactory userSessionFactory;
	private String uidName = UID_NAME;
	private String tokenName = TOKEN_NAME;
	/**
	 * 是否在创建后执行write方法
	 */
	private boolean writeOnCreated = true;

	/**
	 * 一个用户是否只可以存在一个session
	 */
	private boolean singleSession = SINGLE_SESSION;

	public DefaultUserSessionManager(UserSessionFactory userSessionFactory) {
		this.userSessionFactory = userSessionFactory;
	}

	public String getUidName() {
		return uidName;
	}

	public void setUidName(String uidName) {
		Assert.requiredArgument(StringUtils.isNotEmpty(uidName), "uidName");
		this.uidName = uidName;
	}

	public String getTokenName() {
		return tokenName;
	}

	public void setTokenName(String tokenName) {
		Assert.requiredArgument(StringUtils.isNotEmpty(tokenName), "tokenName");
		this.tokenName = tokenName;
	}

	public boolean isWriteOnCreated() {
		return writeOnCreated;
	}

	public void setWriteOnCreated(boolean writeOnCreated) {
		this.writeOnCreated = writeOnCreated;
	}

	public boolean isSingleSession() {
		return singleSession;
	}

	public void setSingleSession(boolean singleSession) {
		this.singleSession = singleSession;
	}

	protected ValueWrapper getParameter(HttpChannel httpChannel, String name) {
		ValueWrapper value = httpChannel.get(name);
		if (value == null || value.isEmpty()) {
			String token = httpChannel.getRequest().getHeaders().getFirst(name);
			if (token == null) {
				HttpCookie httpCookie = WebUtils.getCookie(httpChannel.getRequest(), name);
				if (httpCookie != null) {
					token = httpCookie.getValue();
				}
			}

			if (token != null) {
				value = ValueWrapper.of(token);
			}
		}
		return value;
	}

	@Override
	public <T> UserToken<T> read(HttpChannel httpChannel, Class<T> type) {
		ValueWrapper uid = getParameter(httpChannel, getUidName());
		ValueWrapper token = getParameter(httpChannel, getTokenName());
		if (uid == null || uid.isEmpty() || token == null || token.isEmpty()) {
			return null;
		}

		try {
			return new UserToken<T>(token.getAsString(), uid.getAsObject(type));
		} catch (Throwable e) {
			logger.error(e, "[{}] channel[{}] token[{}] uid[{}]",
					WebUtils.getMessageId(httpChannel.getRequest(), httpChannel.getResponse()), httpChannel,
					token.getAsString(), uid.getAsString());
			return null;
		}

	}

	@Override
	public <T> void write(HttpChannel httpChannel, UserToken<T> userToken) {
		HttpCookie uidCookie = new HttpCookie(getUidName(), userToken.getUid() + "");
		uidCookie.setPath("/");
		HttpCookie tokenCookie = new HttpCookie(getTokenName(), userToken.getToken() + "");
		tokenCookie.setPath("/");
		httpChannel.getResponse().addCookie(uidCookie);
		httpChannel.getResponse().addCookie(tokenCookie);
	}

	@Override
	public <T> UserSession<T> getUserSession(HttpChannel httpChannel, Class<T> type) {
		UserToken<T> userToken = read(httpChannel, type);
		if (userToken == null) {
			return null;
		}

		return userSessionFactory.getUserSession(userToken.getUid(), userToken.getToken());
	}

	@Override
	public <T> UserSession<T> createUserSession(HttpChannel httpChannel, T uid) {
		if (singleSession) {
			UserSessions<T> userSessions = userSessionFactory.getUserSessions(uid);
			if (userSessions != null) {
				userSessions.stream().forEach((s) -> s.invalidate());
			}
		}

		UserSession<T> userSession = userSessionFactory.getUserSession(uid, XUtils.getUUID(), true);
		if (writeOnCreated && userSession.isNew()) {
			write(httpChannel, new UserToken<T>(userSession.getId(), userSession.getUid()));
		}
		return userSession;
	}

}
