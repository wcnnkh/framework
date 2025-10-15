package run.soeasy.framework.io;

/**
 * 当检测到无效的MIME类型时抛出的异常，继承自{@link IllegalArgumentException}，
 * 用于在处理媒体类型（如HTTP Content-Type、文件类型标识等）时，明确标识不符合规范的MIME类型。
 * 
 * <p>该异常包含两个核心信息：
 * <ul>
 * <li>引发异常的具体MIME类型（通过{@link #getMimeType()}获取）；</li>
 * <li>详细的错误原因（如格式错误、不支持的子类型、缺失必要参数等）。</li>
 * </ul>
 * 
 * <p>典型使用场景：
 * <ul>
 * <li>解析HTTP请求头中的Content-Type时，检测到不符合RFC规范的格式（如"text/html;charset= utf-8"存在空格）；</li>
 * <li>验证文件上传的MIME类型时，发现类型格式错误（如"image/png/"尾部多余斜杠）；</li>
 * <li>处理自定义协议中的媒体类型时，检测到不支持的类型格式。</li>
 * </ul>
 * 
 * @see IllegalArgumentException 表示方法接收到非法参数的父类异常
 * @see <a href="https://tools.ietf.org/html/rfc2045">RFC 2045 MIME类型规范定义</a>
 */
public class InvalidMimeTypeException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	/**
	 * 引发异常的无效MIME类型（如"text/;html"、"application/json:"等不符合规范的类型）
	 */
	private final String mimeType;

	/**
	 * 构造一个包含无效MIME类型和详细错误信息的异常
	 * 
	 * @param mimeType 引发异常的MIME类型字符串（可为null，若无法识别具体类型）
	 * @param message  详细错误描述，说明MIME类型无效的具体原因（如"缺少主类型"、"参数格式错误"等）
	 */
	public InvalidMimeTypeException(String mimeType, String message) {
		super("Invalid mime type \"" + mimeType + "\": " + message);
		this.mimeType = mimeType;
	}

	/**
	 * 获取引发异常的无效MIME类型
	 * 
	 * @return 无效的MIME类型字符串；若构造时未指定，可能为null
	 */
	public String getMimeType() {
		return this.mimeType;
	}

}
