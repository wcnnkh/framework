package run.soeasy.framework.core.type;

import java.lang.reflect.Type;

import lombok.NonNull;

/**
 * 无类型解析器，用于表示缺失具体类型信息的场景，
 * 继承自{@link AbstractResolvableType}并实现无类型特性。
 * 该类在类型解析失败或不需要具体类型信息时使用，作为类型系统中的"空类型"标识。
 *
 * <p>核心特性：
 * <ul>
 *   <li>无类型标识：表示缺失具体类型信息的特殊状态</li>
 *   <li>空类型特征：所有类型查询方法返回空或false</li>
 *   <li>基础类型映射：原始类型固定为{@link Object}</li>
 *   <li>类型安全：作为类型系统的空对象模式实现</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>类型解析失败：当无法解析具体类型时作为默认返回值</li>
 *   <li>未指定类型：表示泛型参数未指定的默认状态</li>
 *   <li>动态类型处理：在动态类型场景中作为基础类型标识</li>
 *   <li>空对象模式：作为类型系统中的空对象实现</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 表示未指定类型的场景
 * ResolvableType unknownType = new NoneType(Object.class);
 * System.out.println("原始类型: " + unknownType.getRawType()); // 输出: class java.lang.Object
 * System.out.println("是否为数组: " + unknownType.isArray()); // 输出: false
 * </pre>
 *
 * @see AbstractResolvableType
 * @see ResolvableType
 */
public final class NoneType extends AbstractResolvableType<Type> {

    /**
     * 创建无类型解析器，关联基础类型。
     * <p>
     * 该构造函数用于初始化无类型实例，通常关联Object.class或其他基础类型。
     *
     * @param type 基础类型（通常为Object.class），不可为null
     */
    public NoneType(@NonNull Type type) {
        super(type);
    }

    /**
     * 判断是否包含类型参数（始终返回false）。
     * <p>
     * 无类型不包含任何类型参数，该方法始终返回false。
     *
     * @return false
     */
    @Override
    public boolean hasActualTypeArguments() {
        return false;
    }

    /**
     * 获取类型参数数组（始终返回空数组）。
     * <p>
     * 无类型没有类型参数，该方法始终返回空数组。
     *
     * @return 空的ResolvableType数组
     */
    @Override
    public ResolvableType[] getActualTypeArguments() {
        return EMPTY_TYPES_ARRAY;
    }

    /**
     * 判断是否为数组类型（始终返回false）。
     * <p>
     * 无类型不表示数组，该方法始终返回false。
     *
     * @return false
     */
    @Override
    public boolean isArray() {
        return false;
    }

    /**
     * 获取数组组件类型（始终返回null）。
     * <p>
     * 无类型不是数组，该方法始终返回null。
     *
     * @return null
     */
    @Override
    public ResolvableType getComponentType() {
        return null;
    }

    /**
     * 获取类型变量下界（始终返回空数组）。
     * <p>
     * 无类型没有类型变量，该方法始终返回空数组。
     *
     * @return 空的ResolvableType数组
     */
    @Override
    public ResolvableType[] getLowerBounds() {
        return EMPTY_TYPES_ARRAY;
    }

    /**
     * 获取内部类的外部类型（始终返回null）。
     * <p>
     * 无类型没有外部类型，该方法始终返回null。
     *
     * @return null
     */
    @Override
    public ResolvableType getOwnerType() {
        return null;
    }

    /**
     * 获取原始类型（固定返回Object.class）。
     * <p>
     * 无类型的原始类型映射为Object.class，作为所有类型的基础类型。
     *
     * @return Object.class
     */
    @Override
    public Class<?> getRawType() {
        return Object.class;
    }

    /**
     * 获取类型变量上界（始终返回空数组）。
     * <p>
     * 无类型没有类型变量，该方法始终返回空数组。
     *
     * @return 空的ResolvableType数组
     */
    @Override
    public ResolvableType[] getUpperBounds() {
        return EMPTY_TYPES_ARRAY;
    }
}