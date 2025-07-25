package run.soeasy.framework.core.type;

import java.lang.reflect.TypeVariable;
import java.util.Objects;

import lombok.NonNull;

/**
 * 原始类型解析器，用于表示Java类型系统中的原始类型（Raw Type），
 * 继承自{@link AbstractResolvableType}并针对原始类型特性进行实现。
 * 该类主要用于解析泛型擦除后的类型信息，适用于反射操作、泛型类型解析等场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>原始类型封装：直接包装Class对象表示原始类型，如List.class</li>
 *   <li>泛型参数解析：支持获取原始类型声明的类型变量（如List的E）</li>
 *   <li>数组类型处理：提供数组类型检测和组件类型解析功能</li>
 *   <li>类型变量解析：结合{@link TypeVariableResolver}处理泛型变量解析</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>泛型类型解析：在泛型擦除环境中获取类型信息（如集合元素类型）</li>
 *   <li>反射操作：通过原始类型获取类的类型参数和结构</li>
 *   <li>框架开发：实现类型安全的泛型组件（如数据转换器、容器）</li>
 *   <li>序列化处理：处理泛型类型的序列化与反序列化场景</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 解析List的原始类型
 * RawType listType = new RawType(List.class);
 * System.out.println("原始类型: " + listType.getRawType()); // 输出: class java.util.List
 * 
 * // 检测类型参数
 * if (listType.hasActualTypeArguments()) {
 *     ResolvableType[] args = listType.getActualTypeArguments();
 *     System.out.println("类型参数数量: " + args.length); // 输出: 1
 * }
 * 
 * // 处理数组类型
 * RawType arrayType = new RawType(String[].class);
 * System.out.println("是否为数组: " + arrayType.isArray()); // 输出: true
 * ResolvableType componentType = arrayType.getComponentType();
 * System.out.println("组件类型: " + componentType.getRawType()); // 输出: class java.lang.String
 * </pre>
 *
 * @see AbstractResolvableType
 * @see ResolvableType
 * @see TypeVariableResolver
 */
public class RawType extends AbstractResolvableType<Class<?>> {
    
    /**
     * 创建原始类型解析器（使用默认类型变量解析器）。
     * <p>
     * 自动关联Class对象与默认的类型变量解析器，适用于简单类型解析场景。
     *
     * @param type 原始类型的Class对象（如List.class），不可为null
     * @throws NullPointerException 当type为null时抛出
     */
    public RawType(@NonNull Class<?> type) {
        super(type);
    }

    /**
     * 创建原始类型解析器（指定类型变量解析器）。
     * <p>
     * 允许自定义类型变量解析逻辑，适用于复杂泛型场景（如嵌套泛型解析）。
     *
     * @param type 原始类型的Class对象，不可为null
     * @param typeVariableResolver 类型变量解析器，不可为null
     * @throws NullPointerException 当type或typeVariableResolver为null时抛出
     */
    public RawType(@NonNull Class<?> type, TypeVariableResolver typeVariableResolver) {
        super(type, typeVariableResolver);
    }

    /**
     * 获取原始类型的Class对象（核心标识）。
     * <p>
     * 直接返回封装的Class对象，例如List.class、String[].class等。
     *
     * @return 原始类型的Class对象
     */
    @Override
    public final Class<?> getRawType() {
        return getType();
    }

    /**
     * 判断原始类型是否声明了类型参数（泛型变量）。
     * <p>
     * 实现逻辑：检查Class对象声明的类型参数数量是否大于0。
     * 例：List.class声明了类型参数E，返回true；String.class未声明，返回false。
     *
     * @return true若声明了类型参数，否则false
     */
    @Override
    public boolean hasActualTypeArguments() {
        return getType().getTypeParameters().length != 0;
    }

    /**
     * 获取原始类型声明的类型参数（泛型变量）。
     * <p>
     * 实现流程：
     * <ol>
     *   <li>获取Class对象的TypeVariable数组（如List的E）</li>
     *   <li>通过类型变量解析器转换为ResolvableType数组</li>
     * </ol>
     *
     * @return 类型参数的ResolvableType数组（可能为空）
     */
    @Override
    public ResolvableType[] getActualTypeArguments() {
        TypeVariable<?>[] typeVariables = getType().getTypeParameters();
        return ResolvableType.toResolvableTypes(this.getTypeVariableResolver(), typeVariables);
    }

    /**
     * 判断原始类型是否为数组类型（如String[]）。
     * <p>
     * 实现逻辑：直接调用Class对象的isArray()方法。
     *
     * @return true若为数组类型，否则false
     */
    @Override
    public boolean isArray() {
        return getType().isArray();
    }

    /**
     * 获取数组类型的组件类型（仅当isArray()为true时有效）。
     * <p>
     * 例：String[].class的组件类型为String.class。
     *
     * @return 数组组件类型的ResolvableType，非数组类型返回null
     */
    @Override
    public ResolvableType getComponentType() {
        return ResolvableType.forType(getType().getComponentType(), this.getTypeVariableResolver());
    }

    /**
     * 获取类型变量的下界（原始类型不支持，固定返回空数组）。
     * <p>
     * 泛型擦除后无法获取下界信息，如T extends Number中的Number。
     *
     * @return 空的ResolvableType数组
     */
    @Override
    public ResolvableType[] getLowerBounds() {
        return EMPTY_TYPES_ARRAY;
    }

    /**
     * 获取内部类的外部类型（原始类型不支持，固定返回null）。
     * <p>
     * 例：InnerClass.class的外部类型为OuterClass.class，但原始类型无法获取。
     *
     * @return null
     */
    @Override
    public ResolvableType getOwnerType() {
        return null;
    }

    /**
     * 获取类型变量的上界（原始类型不支持，固定返回空数组）。
     * <p>
     * 泛型擦除后无法获取上界信息，如T extends Number中的Number。
     *
     * @return 空的ResolvableType数组
     */
    @Override
    public ResolvableType[] getUpperBounds() {
        return EMPTY_TYPES_ARRAY;
    }

    /**
     * 重写equals方法（基于原始类型的Class对象比较）。
     * <p>
     * 当且仅当另一个对象为RawType且封装的Class对象相等时返回true。
     *
     * @param o 待比较对象
     * @return true若对象相等，否则false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawType rawType = (RawType) o;
        return Objects.equals(getType(), rawType.getType());
    }

    /**
     * 重写hashCode方法（基于原始类型的Class对象计算）。
     *
     * @return Class对象的哈希值
     */
    @Override
    public int hashCode() {
        return Objects.hash(getType());
    }
}