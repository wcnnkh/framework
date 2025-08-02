package run.soeasy.framework.core.type;

import lombok.NonNull;

/**
 * 实例工厂接口，定义创建对象实例的核心能力。
 * 该接口提供了类型检查和实例创建的标准方法，
 * 允许框架或应用根据需要实现不同的对象实例化策略。
 *
 * <p>核心特性：
 * <ul>
 *   <li>类型检查：通过{@link #canInstantiated(ResolvableType)}方法判断是否能创建指定类型的实例</li>
 *   <li>实例创建：通过{@link #newInstance(ResolvableType)}方法创建指定类型的实例</li>
 *   <li>类型抽象：使用{@link ResolvableType}作为类型描述符，支持泛型等复杂类型</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>依赖注入框架：根据类型自动创建和注入对象</li>
 *   <li>工厂模式实现：集中管理对象的创建逻辑</li>
 *   <li>对象池：实现对象的复用和管理</li>
 *   <li>序列化/反序列化：动态创建对象实例</li>
 * </ul>
 *
 * <p>实现注意事项：
 * <ul>
 *   <li>实现类应确保线程安全，特别是在高并发环境下</li>
 *   <li>对于无法创建的类型，{@link #canInstantiated(ResolvableType)}应返回false而非抛出异常</li>
 *   <li>实例创建过程中应处理可能的异常，避免直接抛出RuntimeException</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建一个简单的String实例工厂
 * InstanceFactory stringFactory = new InstanceFactory() {
 *     public boolean canInstantiated(@NonNull ResolvableType requiredType) {
 *         return requiredType.getRawType() == String.class;
 *     }
 *
 *     public Object newInstance(@NonNull ResolvableType requiredType) {
 *         return ""; // 返回空字符串实例
 *     }
 * };
 *
 * // 使用工厂创建实例
 * ResolvableType stringType = ResolvableType.forClass(String.class);
 * if (stringFactory.canInstantiated(stringType)) {
 *     String instance = (String) stringFactory.newInstance(stringType);
 *     System.out.println("创建的实例: " + instance);
 * }
 * </pre>
 *
 * @see ResolvableType
 */
public interface InstanceFactory {
    /**
     * 判断是否可以创建指定类型的实例。
     * <p>
     * 该方法应快速判断当前工厂是否支持创建指定类型的实例，
     * 而不进行实际的实例创建操作。
     *
     * @param requiredType 需要创建实例的类型描述符，不可为null
     * @return 如果可以创建实例返回true，否则返回false
     */
    boolean canInstantiated(@NonNull ResolvableType requiredType);

    /**
     * 创建指定类型的实例。
     * <p>
     * 调用该方法前应先通过{@link #canInstantiated(ResolvableType)}
     * 方法判断是否可以创建实例。
     *
     * @param requiredType 需要创建实例的类型描述符，不可为null
     * @return 创建的实例对象
     * @throws RuntimeException 如果创建实例过程中发生错误
     */
    Object newInstance(@NonNull ResolvableType requiredType);
}