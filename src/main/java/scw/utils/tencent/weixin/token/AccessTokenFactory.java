package scw.utils.tencent.weixin.token;

public interface AccessTokenFactory {
	String getAppId();

	String getAppSecret();

	String getAccessToken();
}
