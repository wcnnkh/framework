package run.soeasy.framework.core.convert.value;

import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 源数据描述符接口，用于获取数据源的返回类型描述信息。
 * <p>
 * 该接口定义了获取数据源返回类型的标准方法，实现类需提供具体的类型描述实现，
 * 适用于需要明确数据源类型信息的场景，如类型转换、参数校验、数据绑定等。
 * </p>
 *
 * <p><b>核心功能：</b>
 * <ul>
 *   <li>类型信息获取：提供统一的接口获取数据源的返回类型描述</li>
 *   <li>类型安全保障：通过{@link TypeDescriptor}确保类型信息的准确性和完整性</li>
 *   <li>场景适配：适用于各种需要类型描述的框架组件，如转换器、参数解析器等</li>
 * </ul>
 *
 * @author soeasy.run
 * @see TypeDescriptor
 * @see run.soeasy.framework.core.convert.Converter
 */
public interface SourceDescriptor {

    /**
     * 获取数据源的返回类型描述符
     * <p>
     * 返回的{@link TypeDescriptor}包含完整的类型信息，包括：
     * <ol>
     *   <li>原始类型（raw type）</li>
     *   <li>泛型参数（generic arguments）</li>
     *   <li>类型所属的上下文（如声明该类型的类）</li>
     * </ol>
     * 
     * @return 数据源的返回类型描述符，不可为null
     */
    TypeDescriptor getReturnTypeDescriptor();
}