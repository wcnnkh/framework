package run.soeasy.framework.core.convert.value;

import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 目标类型描述符接口，用于定义类型转换的目标类型元信息及非空约束。
 * <p>
 * 该接口提供获取目标类型描述符的标准方法，并支持声明目标位置是否禁止空值，
 * 适用于类型转换、参数绑定、数据校验等需要明确目标类型要求的场景。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型元信息获取：通过{@link #getRequiredTypeDescriptor()}获取目标类型描述符</li>
 *   <li>空值约束声明：通过{@link #isRequired()}定义目标位置是否接受空值</li>
 *   <li>默认实现：提供{@code isRequired()}的默认实现（允许空值），子类可按需覆盖</li>
 * </ul>
 *
 * @author soeasy.run
 * @see TypeDescriptor
 * @see run.soeasy.framework.core.convert.Converter
 */
public interface TargetDescriptor {

    /**
     * 获取目标位置的类型描述符
     * <p>
     * 返回的{@link TypeDescriptor}包含完整的目标类型元信息，包括：
     * <ol>
     *   <li>原始类型（raw type）及泛型参数</li>
     *   <li>类型声明的上下文（如字段所属类、方法返回类型等）</li>
     *   <li>类型层级关系（接口实现、父类继承等）</li>
     * </ol>
     * 
     * @return 目标类型描述符，不可为null
     */
    TypeDescriptor getRequiredTypeDescriptor();

    /**
     * 判断目标位置是否禁止空值
     * <p>
     * 返回true时，类型转换或数据绑定过程中若输入为空值，应抛出异常或拒绝赋值；
     * 返回false时允许空值（默认行为）。
     * 
     * @return true表示目标位置不允许空值，false表示允许空值
     */
    default boolean isRequired() {
        return false;
    }
}