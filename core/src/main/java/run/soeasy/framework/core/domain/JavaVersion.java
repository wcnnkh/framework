package run.soeasy.framework.core.domain;

import java.io.Serializable;
import java.util.Objects;

import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * Java版本工具类，用于解析和比较Java版本信息。
 * 该类提供了对Java版本号的解析、比较和特性检测功能，
 * 支持从"java.version"系统属性自动获取当前运行时的Java版本，
 * 并提供方法判断是否支持特定Java版本或类库。
 *
 * <p>核心特性：
 * <ul>
 *   <li>版本解析：支持解析标准Java版本号格式（如"1.8.0_291"、"11.0.12"等）</li>
 *   <li>版本比较：提供精确的版本号比较功能</li>
 *   <li>特性检测：支持检测当前Java版本是否为特定版本（如Java 8、Java 11等）</li>
 *   <li>兼容性检查：支持检查类或类库与当前Java版本的兼容性</li>
 * </ul>
 *
 * <p>版本解析规则：
 * <ul>
 *   <li>对于Java 9及以上版本（版本号格式如"9"、"11.0.1"），会自动转换为标准格式"1.9.0"等</li>
 *   <li>对于Java 8及以下版本（版本号格式如"1.8.0_291"），保持原始格式解析</li>
 *   <li>主版本号（master version）始终作为兼容性判断的核心依据</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 获取当前Java版本
 * JavaVersion currentVersion = JavaVersion.INSTANCE;
 * System.out.println("当前Java版本: " + currentVersion);
 * 
 * // 判断是否为Java 8
 * if (currentVersion.isJava8()) {
 *     System.out.println("当前运行在Java 8环境");
 * }
 * 
 * // 检查类的兼容性
 * boolean compatible = currentVersion.isSupported(MyClass.class);
 * System.out.println("MyClass与当前Java版本兼容: " + compatible);
 * </pre>
 *
 * @author soeasy.run
 * @see JoinVersion
 * @see Version
 */
public final class JavaVersion extends JoinVersion implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /** 当前运行时的Java版本单例 */
    public static final JavaVersion INSTANCE = parse(System.getProperty("java.version"));

    /**
     * 解析Java版本号字符串为JavaVersion对象。
     * <p>
     * 解析规则：
     * <ol>
     *   <li>使用点号（.）分割版本号字符串</li>
     *   <li>对于Java 9及以上版本（主版本号&gt;1），自动在前面添加"1."以保持格式统一</li>
     *   <li>例如："9"会被解析为"1.9.0"，"11.0.1"会被解析为"1.11.0.1"</li>
     * </ol>
     *
     * @param version 版本号字符串（如"1.8.0_291"、"11.0.12"等）
     * @return 解析后的JavaVersion对象
     */
    public static JavaVersion parse(String version) {
        CharSequenceTemplate versionTemplate = new CharSequenceTemplate(version, ".");
        Version[] array = versionTemplate.getAsElements().toArray(Version[]::new);
        
        // 处理Java 9及以上版本（主版本号>1）
        if (array.length > 1) {
            Version fragment = array[0];
            if (fragment.isNumber() && fragment.getAsInt() > 1) {
                Version[] fragments = new Version[array.length + 1];
                fragments[0] = new IntValue(1);
                System.arraycopy(array, 0, fragments, 1, array.length);
                array = fragments;
            }
        }
        
        // 提取主版本号（master version），通常是第二位（索引1）
        Version master = array.length > 1 ? array[1] : array[0];
        return new JavaVersion(Streamable.array(array), versionTemplate.getDelimiter(), master);
    }

    /** 主版本号，用于兼容性判断的核心依据 */
    private final Version master;

    /**
     * 私有构造函数，使用工厂方法{@link #parse(String)}创建实例。
     *
     * @param elements 版本号片段集合
     * @param delimiter 版本号分隔符
     * @param master 主版本号
     */
    private JavaVersion(Streamable<Version> elements, CharSequence delimiter, Version master) {
        super(elements, delimiter);
        this.master = Objects.requireNonNull(master, "主版本号不能为null");
    }

    /**
     * 获取主版本号。
     *
     * @return 主版本号对象
     */
    public Version getMaster() {
        return master;
    }

    /**
     * 判断是否为Java 5版本。
     *
     * @return 如果主版本号为5返回true，否则返回false
     */
    public boolean isJava5() {
        return master.getAsInt() == 5;
    }

    /**
     * 判断是否为Java 6版本。
     *
     * @return 如果主版本号为6返回true，否则返回false
     */
    public boolean isJava6() {
        return master.getAsInt() == 6;
    }

    /**
     * 判断是否为Java 7版本。
     *
     * @return 如果主版本号为7返回true，否则返回false
     */
    public boolean isJava7() {
        return master.getAsInt() == 7;
    }

    /**
     * 判断是否为Java 8版本。
     *
     * @return 如果主版本号为8返回true，否则返回false
     */
    public boolean isJava8() {
        return master.getAsInt() == 8;
    }

    /**
     * 判断当前Java版本是否支持指定的最低版本。
     *
     * @param version 最低支持的版本号（如8表示Java 8及以上）
     * @return 如果当前主版本号大于等于指定版本返回true，否则返回false
     */
    public boolean isSupported(int version) {
        return version >= master.getAsInt();
    }

    /**
     * 判断指定类是否与当前Java版本兼容。
     * <p>
     * 兼容性检查规则：
     * <ol>
     *   <li>检查类的包实现版本和规范版本</li>
     *   <li>递归检查类实现的所有接口的兼容性</li>
     *   <li>如果任何检查失败，则认为类不兼容</li>
     * </ol>
     *
     * @param clazz 要检查的类，不可为null
     * @return 如果类与当前Java版本兼容返回true，否则返回false
     */
    public boolean isSupported(@NonNull Class<?> clazz) {
        Package pkg = clazz.getPackage();
        if (pkg == null) {
            // 未知包的类默认认为兼容
            return true;
        }

        // 检查包实现版本
        String implementationVersion = pkg.getImplementationVersion();
        if (StringUtils.isNotEmpty(implementationVersion)) {
            JavaVersion version = parse(implementationVersion);
            if (version.getMaster().compareTo(this.master) > 0) {
                return false;
            }
        }

        // 检查包规范版本
        String specificationVersion = pkg.getSpecificationVersion();
        if (StringUtils.isNotEmpty(specificationVersion)) {
            JavaVersion version = parse(specificationVersion);
            if (version.getMaster().compareTo(this.master) > 0) {
                return false;
            }
        }

        // 递归检查所有接口的兼容性
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            if (!isSupported(interfaceClass)) {
                return false;
            }
        }
        return true;
    }
}