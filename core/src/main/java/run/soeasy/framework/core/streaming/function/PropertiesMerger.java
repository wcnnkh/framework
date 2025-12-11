package run.soeasy.framework.core.streaming.function;

import java.util.Map;
import java.util.Properties;

import run.soeasy.framework.core.streaming.Streamable;

/**
 * Properties合并器，用于将多个Properties实例合并为一个Properties对象。
 * 该合并器按顺序处理输入的Properties集合，将每个Properties中的键值对依次添加到结果中，
 * 若存在相同键，后出现的Properties中的值会覆盖先出现的Properties中的值。
 *
 * @author soeasy.run
 * @see Merger
 * @see Properties
 * @see Streamable
 */
public class PropertiesMerger implements Merger<Properties> {
    
    /**
     * 单例实例，用于全局共享的Properties合并器。
     * 该实例为无泛型参数的原始类型，可安全地用于任何Properties合并场景。
     */
    static final PropertiesMerger INSTANCE = new PropertiesMerger();

    /**
     * 将多个Properties合并为一个Properties对象。
     * 该方法执行以下操作：
     * 1. 创建一个空的Properties作为结果容器
     * 2. 遍历输入的Properties集合，依次调用putAll方法合并键值对
     * 3. 最终返回合并后的Properties，若输入为空则返回空Properties
     *
     * @param elements 待合并的Properties集合，元素可为null，但null元素会导致putAll抛出NPE
     * @return 合并后的Properties对象，保证非null
     * @throws NullPointerException 当输入集合中存在null元素时抛出
     * @see Properties#putAll(Map)
     */
    @Override
    public Properties select(Streamable<Properties> elements) {
        Properties properties = new Properties();
        for (Properties props : elements.toCollection()) {
            properties.putAll(props);
        }
        return properties;
    }
}