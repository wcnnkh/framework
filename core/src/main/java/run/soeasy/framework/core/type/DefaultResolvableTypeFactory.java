package run.soeasy.framework.core.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import lombok.NonNull;

/**
 * 默认的可解析类型工厂实现，根据Java反射Type创建对应的ResolvableType实例。
 * 该工厂支持处理ParameterizedType、WildcardType、TypeVariable、Class和GenericArrayType等类型，
 * 并提供单例模式以提高性能和减少资源消耗。
 *
 * <p>核心特性：
 * <ul>
 *   <li>类型识别：自动识别不同的Java反射Type类型</li>
 *   <li>类型转换：将Java反射Type转换为对应的ResolvableType实现</li>
 *   <li>单例模式：通过静态常量INSTANCE提供单例实例</li>
 *   <li>扩展性：支持通过TypeVariableResolver处理类型变量</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>泛型处理：解析泛型类型信息</li>
 *   <li>反射操作：在反射过程中获取和处理类型信息</li>
 *   <li>框架开发：作为类型系统的基础组件</li>
 *   <li>序列化/反序列化：处理泛型类型的序列化和反序列化</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 获取工厂实例
 * ResolvableTypeFactory factory = DefaultResolvableTypeFactory.INSTANCE;
 * 
 * // 创建ParameterizedType的ResolvableType
 * ParameterizedType listOfStringType = ...; // 通过反射获取
 * ResolvableType resolvableType = factory.createResolvableType(listOfStringType, null);
 * 
 * // 获取原始类型
 * Class&lt;?&gt; rawType = resolvableType.getRawType();
 * 
 * // 获取泛型参数
 * ResolvableType[] generics = resolvableType.getGenerics();
 * </pre>
 *
 * @see ResolvableTypeFactory
 * @see ResolvableType
 */
public class DefaultResolvableTypeFactory implements ResolvableTypeFactory {
    /** 单例实例，用于全局共享 */
    public static final DefaultResolvableTypeFactory INSTANCE = new DefaultResolvableTypeFactory();

    /**
     * 根据Java反射Type创建对应的ResolvableType实例。
     * <p>
     * 该方法支持以下Type类型：
     * <ul>
     *   <li>ParameterizedType：转换为ResolvableParameterizedType</li>
     *   <li>WildcardType：转换为ResolvableWildcardType</li>
     *   <li>TypeVariable：转换为ResolvableTypeVariable</li>
     *   <li>Class：转换为RawType</li>
     *   <li>GenericArrayType：转换为ArrayType</li>
     * </ul>
     * 对于不支持的Type类型，返回NoneType。
     *
     * @param type 要转换的Java反射Type，不可为null
     * @param resolver 类型变量解析器，可为null
     * @return 对应的ResolvableType实例
     */
    @Override
    public @NonNull ResolvableType createResolvableType(@NonNull Type type, TypeVariableResolver resolver) {
        if (type instanceof ParameterizedType) {
            return new ResolvableParameterizedType((ParameterizedType) type, resolver);
        } else if (type instanceof WildcardType) {
            return new ResolvableWildcardType((WildcardType) type, resolver);
        } else if (type instanceof TypeVariable) {
            return new ResolvableTypeVariable((TypeVariable<?>) type, resolver);
        } else if (type instanceof Class) {
            return new RawType((Class<?>) type, resolver);
        } else if (type instanceof GenericArrayType) {
            return new ArrayType((GenericArrayType) type, resolver);
        }
        return new NoneType(type);
    }

}