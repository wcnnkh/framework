package run.soeasy.framework.core.convert;

/**
 * 转换器感知接口，用于标识支持转换器注入的组件。
 * 实现该接口的类可以接收{@link Converter}实例，
 * 从而获得类型转换能力，实现组件与转换器的解耦。
 *
 * <p>设计目的：
 * <ul>
 *   <li>提供统一的转换器注入标准</li>
 *   <li>支持组件动态设置转换器实现</li>
 *   <li>分离组件逻辑与转换实现，符合依赖倒置原则</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <pre>
 * public class DataProcessor implements ConverterAware {
 *     private Converter converter;
 *     
 *     &#64;Override
 *     public void setConverter(Converter converter) {
 *         this.converter = converter;
 *     }
 *     
 *     public &lt;T&gt; T process(String input, Class&lt;T&gt; targetType) {
 *         return converter.convert(input, targetType);
 *     }
 * }
 * </pre>
 *
 * @author soeasy.run
 * @see Converter
 */
public interface ConverterAware {

    /**
     * 设置类型转换器
     * 
     * @param converter 类型转换器实例，不可为null
     */
    void setConverter(Converter converter);
}