package run.soeasy.framework.core.convert.value;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 可访问描述符接口，整合源数据与目标数据的描述能力，支持读写权限判断。
 * <p>
 * 该接口继承自{@link SourceDescriptor}和{@link TargetDescriptor}，
 * 既可以描述数据源的类型信息，也可以定义目标位置的类型要求，
 * 同时提供读写权限的判断能力，适用于需要双向类型描述和访问控制的场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>双向类型描述：同时具备源数据和目标数据的类型描述能力</li>
 *   <li>访问权限控制：支持判断数据位置的可读/可写状态</li>
 *   <li>类型安全封装：通过{@link TypeDescriptor}确保类型信息的完整性</li>
 *   <li>工厂方法创建：提供静态工厂方法快速生成实例</li>
 * </ul>
 *
 * @author soeasy.run
 * @see SourceDescriptor
 * @see TargetDescriptor
 * @see TypeDescriptor
 */
public interface AccessibleDescriptor extends SourceDescriptor, TargetDescriptor {

    /**
     * 创建基于类型描述符的可访问描述符实例
     * <p>
     * 工厂方法，根据指定的{@link TypeDescriptor}创建一个基本实现，
     * 适用于仅需要类型描述而无需额外访问控制逻辑的场景。
     * 
     * @param typeDescriptor 类型描述符，不可为null
     * @return 可访问描述符实例
     * @throws NullPointerException 若typeDescriptor为null
     */
    public static AccessibleDescriptor forTypeDescriptor(@NonNull TypeDescriptor typeDescriptor) {
        return new CustomizeAccessibleDescriptor(typeDescriptor);
    }

    /**
     * 判断数据位置是否可读
     * <p>
     * 默认返回true，子类可重写此方法实现具体的可读逻辑，
     * 例如根据权限注解、字段修饰符等判断是否允许读取。
     * 
     * @return true表示可读，false表示不可读
     */
    default boolean isReadable() {
        return true;
    }

    /**
     * 判断数据位置是否可写
     * <p>
     * 默认返回true，子类可重写此方法实现具体的可写逻辑，
     * 例如根据字段是否为final、是否有setter方法等判断是否允许写入。
     * 
     * @return true表示可写，false表示不可写
     */
    default boolean isWriteable() {
        return true;
    }
}