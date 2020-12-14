package scw.tencent.wx;

import scw.oauth2.AccessToken;
import scw.security.Token;

public interface TokenFactory extends WXConstants{
	String getAppId();

	String getAppSecret();

	AccessToken getAccessToken();

	AccessToken getAccessToken(String type);

	Token getJsApiTicket();

	Token getTicket(String type);
}
