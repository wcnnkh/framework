package scw.security.jwt;

/**
 * jwt头
 * @author shuchaowen
 *
 */
public interface Header {
	/**
	 * 表示签名使用的算法，默认为HMAC SHA256（写为HS256）
	 * @return
	 */
	String getAig();
	
	/**
	 * 表示令牌的类型，JWT令牌统一写为JWT
	 * @return
	 */
	String getTyp();
}
