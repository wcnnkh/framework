package scw.jwt;

import java.util.Map;

/**
 * jwt主体
 * @author shuchaowen
 *
 */
public interface Payload extends Map<String, String>{
	/**
	 * 发行人
	 * @return
	 */
	String getIssuer();
	
	/**
	 * 到期时间
	 * @return
	 */
	String getExpirationTime();
	
	/**
	 * 主题
	 * @return
	 */
	String getSubject();
	
	/**
	 * 受众/用户
	 * @return
	 */
	String getAudience();
	
	/**
	 * 生效时间/在此之前不可用
	 * @return
	 */
	String getNotBefore();
	
	/**
	 * 签发时间
	 * @return
	 */
	String getIssuedAt();
	
	/**
	 * 编号/JWT ID用于标识该JWT
	 * @return
	 */
	String getJwtId();
}
