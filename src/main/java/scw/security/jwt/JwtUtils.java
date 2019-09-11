package scw.security.jwt;

//TODO 还未完成
public final class JwtUtils {
	private JwtUtils(){};
	
	/**
	 * 获取签名
	 * @param header
	 * @param payload
	 * @param secret 密钥
	 * @return
	 */
	public static String getSignature(Header header, Payload payload, String secret){
		return null;
	}
}
