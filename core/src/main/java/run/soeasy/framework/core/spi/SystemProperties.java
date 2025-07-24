package run.soeasy.framework.core.spi;

import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.transform.property.PropertyAccessor;
import run.soeasy.framework.core.transform.property.TypedProperties;

/**
 * 系统属性访问器，实现{@link TypedProperties}接口，
 * 提供统一的类型安全接口访问Java系统属性和环境变量，支持属性值的类型转换。
 * <p>
 * 该类采用单例模式实现，通过双重检查锁定确保线程安全的实例化。
 * 它将系统属性和环境变量合并为统一的属性集合，并提供类型安全的访问方法。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>统一访问：将系统属性（{@code System.getProperties()}）和环境变量（{@code System.getenv()}）合并为单一属性集合</li>
 *   <li>类型安全：通过{@link ConversionService}支持属性值的自动类型转换</li>
 *   <li>线程安全：使用双重检查锁定实现线程安全的单例模式</li>
 *   <li>延迟加载：首次使用时才初始化实例，提高应用启动性能</li>
 * </ul>
 * </p>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>配置管理：统一访问系统属性和环境变量中的配置信息</li>
 *   <li>类型安全访问：以类型安全的方式获取系统属性值</li>
 *   <li>动态配置：在运行时修改系统属性值</li>
 *   <li>框架集成：作为框架内部获取系统配置的统一接口</li>
 * </ul>
 * </p>
 *
 * <p><b>访问优先级：</b>
 * <ol>
 *   <li>系统属性（{@code System.getProperties()}）</li>
 *   <li>环境变量（{@code System.getenv()}）</li>
 * </ol>
 * 当系统属性和环境变量中存在相同名称的属性时，优先返回系统属性的值。
 * </p>
 *
 * @author soeasy.run
 * @see TypedProperties
 * @see PropertyAccessor
 * @see ConversionService
 */
public final class SystemProperties implements TypedProperties {
    /** 字符串类型描述符常量 */
    private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);
    
    /** 单例实例（延迟加载） */
    private static volatile SystemProperties instance;

    /**
     * 获取系统属性访问器的单例实例
     * <p>
     * 该方法使用双重检查锁定确保在多线程环境下安全地创建单例实例。
     * </p>
     * 
     * @return 系统属性访问器的单例实例
     */
    public static SystemProperties getInstance() {
        if (instance == null) {
            synchronized (SystemProperties.class) {
                if (instance == null) {
                    instance = new SystemProperties();
                }
            }
        }
        return instance;
    }

    /**
     * 私有构造函数，防止外部实例化
     */
    private SystemProperties() {
    }

    /**
     * 获取所有系统属性的迭代器
     * <p>
     * 该迭代器包含系统属性和环境变量的所有键对应的属性访问器。
     * </p>
     * 
     * @return 属性访问器的迭代器
     */
    @Override
    public Iterator<PropertyAccessor> iterator() {
        return keys().map((key) -> get(key)).iterator();
    }

    /**
     * 获取指定名称的系统属性访问器
     * <p>
     * 该访问器可用于读取和修改系统属性值，并支持类型转换。
     * </p>
     * 
     * @param key 属性名称，不可为null
     * @return 属性访问器实例
     */
    @Override
    public PropertyAccessor get(Object key) {
        String keyToUse = String.valueOf(key);
        return new SystemProperty(keyToUse);
    }

    /**
     * 获取所有系统属性和环境变量的键集合
     * <p>
     * 该集合包含系统属性和环境变量的所有键，并自动去重。
     * </p>
     * 
     * @return 包含所有键的元素集合
     */
    @Override
    public Elements<Object> keys() {
        Elements<Object> systemKeys = Elements.of(System.getProperties().stringPropertyNames());
        Elements<Object> envKeys = Elements.of(System.getenv().keySet());
        return systemKeys.concat(envKeys).distinct();
    }

    /**
     * 系统属性访问器的内部实现
     * <p>
     * 该类表示单个系统属性或环境变量的访问器，提供对属性值的类型安全访问。
     * </p>
     */
    @RequiredArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    private static class SystemProperty implements PropertyAccessor {
        /** 属性名称，不可为null */
        @NonNull
        private final String name;
        
        /** 类型转换服务，默认为系统转换服务 */
        @NonNull
        private ConversionService conversionService = SystemConversionService.getInstance();

        /**
         * 设置系统属性的值
         * <p>
         * 该方法将输入值转换为字符串后设置为系统属性值。
         * 注意：环境变量不可通过此方法修改。
         * </p>
         * 
         * @param source 要设置的值
         * @throws UnsupportedOperationException 当尝试修改环境变量时抛出
         */
        @Override
        public void set(Object source) throws UnsupportedOperationException {
            String value = (String) conversionService.convert(source, STRING_TYPE_DESCRIPTOR);
            System.setProperty(name, value);
        }

        /**
         * 获取系统属性或环境变量的值
         * <p>
         * 优先从系统属性中获取值，若不存在则从环境变量中获取。
         * </p>
         * 
         * @return 属性值，若不存在则返回null
         */
        @Override
        public Object get() {
            String value = System.getProperty(name);
            if (value == null) {
                value = System.getenv(name); // 修正：使用name而非value作为参数
            }
            return value;
        }

        /**
         * 判断属性是否可读
         * <p>
         * 若属性值存在（不为null）则返回true，否则返回false。
         * </p>
         * 
         * @return 若属性可读返回true，否则返回false
         */
        @Override
        public boolean isReadable() {
            return get() != null;
        }

        /**
         * 获取返回值的类型描述符
         * <p>
         * 系统属性和环境变量的值类型始终为字符串。
         * </p>
         * 
         * @return 字符串类型描述符
         */
        @Override
        public TypeDescriptor getReturnTypeDescriptor() {
            return STRING_TYPE_DESCRIPTOR;
        }

        /**
         * 获取期望的属性值类型描述符
         * <p>
         * 设置系统属性时，期望的值类型为字符串。
         * </p>
         * 
         * @return 字符串类型描述符
         */
        @Override
        public TypeDescriptor getRequiredTypeDescriptor() {
            return STRING_TYPE_DESCRIPTOR;
        }
    }
}