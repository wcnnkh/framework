package run.soeasy.framework.messaging;

import run.soeasy.framework.io.InvalidMimeTypeException;

/**
 * 无效媒体类型异常，当媒体类型字符串不符合规范（如格式错误、包含非法字符、质量因子无效等）时抛出，
 * 继承自{@link IllegalArgumentException}，用于标识媒体类型解析或验证过程中的错误。
 * 
 * <p>该异常关联无效的媒体类型字符串，并提供访问该字符串的方法，便于问题排查。
 * 支持包装底层的{@link InvalidMimeTypeException}，实现MIME类型异常到媒体类型异常的转换。
 * 
 * @author soeasy.run
 * @see MediaType
 * @see InvalidMimeTypeException
 */
public class InvalidMediaTypeException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    /**
     * 引发异常的无效媒体类型字符串
     */
    private final String mediaType;

    /**
     * 创建无效媒体类型异常，关联指定的无效媒体类型字符串和详细错误信息
     * 
     * @param mediaType 无效的媒体类型字符串（如"invalid/type;q=2.0"，可能为null）
     * @param message 详细错误信息（说明无效的原因，如"质量因子必须在0.0-1.0之间"）
     */
    public InvalidMediaTypeException(String mediaType, String message) {
        super("Invalid media type \"" + mediaType + "\": " + message);
        this.mediaType = mediaType;
    }

    /**
     * 包装{@link InvalidMimeTypeException}为当前异常，用于将MIME类型解析异常转换为媒体类型异常
     * 
     * @param ex 底层的MIME类型解析异常（非空）
     */
    InvalidMediaTypeException(InvalidMimeTypeException ex) {
        super(ex.getMessage(), ex);
        this.mediaType = ex.getMimeType();
    }

    /**
     * 获取引发异常的无效媒体类型字符串
     * 
     * @return 无效的媒体类型字符串，可能为null
     */
    public String getMediaType() {
        return this.mediaType;
    }

}