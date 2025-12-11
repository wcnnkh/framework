package run.soeasy.framework.core.type;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * 不可变类型注册器，采用单例模式设计，预注册Java中核心的不可变类型匹配规则。
 * 继承自{@link TypePredicateRegistry}，复用父类{@link #registerType(Type)}方法
 * 完成具体不可变类型的注册，实现不可变类型的统一匹配。
 * <p>
 * 核心能力：
 * 1. {@link #system()}：获取全局唯一的单例实例，初始化时预注册基础不可变类型；
 * 2. {@link #create()}：创建新实例并复用全局单例的注册规则，支持自定义扩展；
 * </p>
 * 预注册的不可变类型包含：
 * 1. 基本类型及其包装类（依赖项目中已定义的ClassUtils工具类匹配）；
 * 2. 枚举类型（依赖项目中已定义的ClassUtils工具类匹配）；
 * 3. String、大数类型（BigInteger/BigDecimal）、Java.time时间类型、UUID、URI等基础不可变类型；
 *
 * @author soeasy.run
 */
public class ImmutableTypeRegistry extends TypePredicateRegistry {

    /**
     * 全局单例实例，volatile修饰保证多线程下的可见性
     */
    private static volatile ImmutableTypeRegistry registry;

    /**
     * 获取全局唯一的不可变类型注册器单例（线程安全）。
     * 采用双重校验锁实现懒加载，首次调用时初始化并注册所有基础不可变类型规则。
     *
     * @return 全局唯一的ImmutableTypeRegistry单例实例
     */
    public static ImmutableTypeRegistry system() {
        if (registry == null) {
            synchronized (ImmutableTypeRegistry.class) {
                if (registry == null) {
                    registry = new ImmutableTypeRegistry();
                    // 1. 注册：基本类型及其包装类（复用项目已定义的ClassUtils）
                    registry.register(ClassUtils::isPrimitiveOrWrapper);
                    // 2. 注册：枚举类型（复用项目已定义的ClassUtils）
                    registry.register(ClassUtils::isEnum);

                    // 3. 注册：单个不可变类型（复用父类registerType方法）
                    registry.registerType(String.class);
                    registry.registerType(BigInteger.class);
                    registry.registerType(BigDecimal.class);
                    registry.registerType(LocalDate.class);
                    registry.registerType(LocalTime.class);
                    registry.registerType(LocalDateTime.class);
                    registry.registerType(ZonedDateTime.class);
                    registry.registerType(Instant.class);
                    registry.registerType(UUID.class);
                    registry.registerType(URI.class);
                }
            }
        }
        return registry;
    }

    /**
     * 创建新的不可变类型注册器实例，并复用全局单例的所有注册规则。
     * 新实例可基于全局规则扩展自定义不可变类型，不影响全局单例。
     *
     * @return 新的ImmutableTypeRegistry实例（包含全局单例的所有注册规则）
     */
    public static ImmutableTypeRegistry create() {
        ImmutableTypeRegistry registry = new ImmutableTypeRegistry();
        registry.register(system());
        return registry;
    }
}