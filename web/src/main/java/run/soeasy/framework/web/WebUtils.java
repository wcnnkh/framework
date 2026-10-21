package run.soeasy.framework.web;

import org.springframework.http.HttpMethod;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WebUtils extends org.springframework.web.util.WebUtils{
	/**
	 * 判断指定 HTTP 请求方法是否允许携带请求体（Body）。
	 * 
	 * <p>
	 * 根据 HTTP 协议规范，以下方法允许携带 Body：
	 * <ul>
	 * <li>{@link HttpMethod#POST}：标准提交方法，常用于表单、JSON 数据提交</li>
	 * <li>{@link HttpMethod#PUT}：全量更新资源，通常携带完整资源数据</li>
	 * <li>{@link HttpMethod#PATCH}：部分更新资源，携带需修改的字段数据</li>
	 * <li>{@link HttpMethod#DELETE}：删除资源（部分场景下需携带删除条件，虽非标准但实际常用）</li>
	 * </ul>
	 * 其他方法（如 GET、HEAD、OPTIONS 等）不建议携带 Body，多数服务器会忽略该部分数据。
	 *
	 * @param httpMethod HTTP 请求方法实例（不可为 null，由 {@link NonNull} 注解强制约束，传入 null 会抛出
	 *                   {@link NullPointerException}）
	 * @return true：允许携带请求体；false：不允许携带请求体
	 * @throws NullPointerException 若 httpMethod 为 null
	 */
	public static boolean isAllowedBody(@NonNull HttpMethod httpMethod) {
		return httpMethod == HttpMethod.POST || httpMethod == HttpMethod.PUT || httpMethod == HttpMethod.PATCH
				|| httpMethod == HttpMethod.DELETE;
	}
}
