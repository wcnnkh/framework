package run.soeasy.framework.core.convert;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;

/**
 * 类型转换失败异常，用于表示转换过程中发生的具体失败场景。
 * 该异常包含源类型、目标类型和转换值等上下文信息，便于问题定位和调试。
 *
 * <p>核心特性：
 * <ul>
 *   <li>上下文信息：包含源类型、目标类型和转换值</li>
 *   <li>异常链支持：保留原始异常原因</li>
 *   <li>详细错误消息：自动生成包含类型和值的可读错误信息</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * try {
 *     converter.convert("not a number", String.class, Integer.class);
 * } catch (ConversionFailedException ex) {
 *     System.err.println("转换失败: " + ex.getMessage());
 *     System.err.println("源类型: " + ex.getSourceType());
 *     System.err.println("目标类型: " + ex.getTargetType());
 *     System.err.println("转换值: " + ex.getValue());
 * }
 * }</pre>
 *
 * @author soeasy.run
 * @see ConversionException
 */
@Getter
public class ConversionFailedException extends ConversionException {
    private static final long serialVersionUID = 1L;
    
    /** 源类型描述符，不可为null */
    private final TypeDescriptor sourceType;
    
    /** 目标类型描述符，不可为null */
    private final TypeDescriptor targetType;
    
    /** 转换的值，可为null */
    private final Object value;

    /**
     * 创建一个新的转换失败异常
     * 
     * @param sourceType 源类型描述符，不可为null
     * @param targetType 目标类型描述符，不可为null
     * @param value 转换的值，可为null
     * @param cause 原始异常原因，可为null
     * @throws NullPointerException 若sourceType或targetType为null
     */
    public ConversionFailedException(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType, Object value,
            Throwable cause) {
        super(buildErrorMessage(sourceType, targetType, value), cause);
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.value = value;
    }

    /**
     * 构建详细的错误消息
     * 
     * @param sourceType 源类型描述符
     * @param targetType 目标类型描述符
     * @param value 转换的值
     * @return 格式化的错误消息
     */
    private static String buildErrorMessage(TypeDescriptor sourceType, TypeDescriptor targetType, Object value) {
        return "Failed to convert from type [" + sourceType + "] to type [" + targetType + "] " +
               "for value '" + ObjectUtils.toString(value) + "'";
    }

    /**
     * 获取转换值的字符串表示（处理null值）
     * 
     * @return 转换值的字符串表示，null转换为"null"
     */
    public String getValueAsString() {
        return ObjectUtils.toString(value);
    }
}