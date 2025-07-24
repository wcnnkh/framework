package run.soeasy.framework.core.convert;

import lombok.NonNull;

/**
 * 转换器未找到异常，当不存在能够处理指定类型转换的转换器时抛出。
 * 该异常包含源类型和目标类型信息，便于快速定位缺失的转换器。
 *
 * <p>使用场景：
 * <ul>
 *   <li>当调用{@link Converter#convert}方法时，没有找到匹配的转换器</li>
 *   <li>类型转换系统初始化时检测到缺失必要的转换器</li>
 * </ul>
 *
 * <p>示例错误消息：
 * <pre>{@code
 * No converter found capable of converting from type [java.lang.String] to type [java.lang.Integer]
 * }</pre>
 *
 * @author soeasy.run
 * @see ConversionException
 * @see Converter
 */
public class ConverterNotFoundException extends ConversionException {
    private static final long serialVersionUID = 1L;

    /** 源类型描述符，不可为null */
    private final TypeDescriptor sourceType;

    /** 目标类型描述符，不可为null */
    private final TypeDescriptor targetType;

    /**
     * 创建转换器未找到异常
     * 
     * @param sourceType 源类型描述符
     * @param targetType 目标类型描述符
     * @throws NullPointerException 若sourceType或targetType为null
     */
    public ConverterNotFoundException(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
        super(buildErrorMessage(sourceType, targetType));
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    /**
     * 构建详细的错误消息
     * 
     * @param sourceType 源类型描述符
     * @param targetType 目标类型描述符
     * @return 格式化的错误消息
     */
    private static String buildErrorMessage(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return "No converter found capable of converting from type [" + sourceType + "] to type [" + targetType + "]";
    }

    /**
     * 获取源类型描述符
     * 
     * @return 源类型描述符
     */
    public TypeDescriptor getSourceType() {
        return sourceType;
    }

    /**
     * 获取目标类型描述符
     * 
     * @return 目标类型描述符
     */
    public TypeDescriptor getTargetType() {
        return targetType;
    }
}