package run.soeasy.framework.json;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * JSON对象实现类，继承自{@link LinkedHashMap<String, JsonElement>}并实现{@link JsonElement}接口，
 * 用于表示JSON格式中的对象结构（键值对集合），通过LinkedHashMap保持键的插入顺序，符合JSON对象的特性。
 * 
 * <p>该类负责将键值对集合序列化为符合JSON规范的对象字符串，支持键的转义处理和键值对的格式化输出，
 * 是构建和操作JSON对象的核心组件。
 * 
 * @author soeasy.run
 * @see JsonElement
 * @see LinkedHashMap
 */
public class JsonObject extends LinkedHashMap<String, JsonElement> implements JsonElement {

    /**
     * 序列化版本号，确保LinkedHashMap在序列化与反序列化过程中的兼容性
     */
    private static final long serialVersionUID = 1L;

    /**
     * JSON对象的起始符号（固定为'{'）
     */
    public static final char PREFIX = '{';

    /**
     * JSON对象的结束符号（固定为'}'）
     */
    public static final char SUFFIX = '}';

    /**
     * 将JSON对象导出为符合JSON语法的字符串
     * 
     * <p>处理流程：
     * 1. 输出对象起始符'{'；
     * 2. 遍历所有键值对（{@link Entry<String, JsonElement>}）：
     *    - 键用双引号（"）包裹，且通过{@link JsonElement#escaping(String)}处理特殊字符转义；
     *    - 键后添加冒号（:）分隔键与值；
     *    - 调用值的{@link JsonElement#export(Appendable)}方法导出值的JSON表示；
     *    - 多个键值对间添加逗号和空格（", "）作为分隔符；
     * 3. 输出对象结束符'}'。
     * 
     * @param target 接收导出数据的Appendable（如StringBuilder、Writer等）
     * @throws IOException 当Appendable操作失败时抛出（如IO写入错误）
     */
    @Override
    public void export(Appendable target) throws IOException {
        target.append(PREFIX); // 输出JSON对象起始符
        Iterator<Entry<String, JsonElement>> iterator = entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, JsonElement> entry = iterator.next();
            
            // 处理键：添加双引号并转义特殊字符
            target.append('"');
            target.append(JsonElement.escaping(entry.getKey()));
            target.append('"');
            
            // 键值分隔符
            target.append(':');
            
            // 导出值（递归调用值的export方法）
            entry.getValue().export(target);
            
            // 多个键值对间添加逗号和空格（最后一个元素后不添加）
            if (iterator.hasNext()) {
                target.append(',');
                target.append(' ');
            }
        }
        target.append(SUFFIX); // 输出JSON对象结束符
    }

    /**
     * 返回JSON对象的字符串表示（即序列化后的JSON格式字符串）
     * 
     * @return 符合JSON规范的对象字符串
     */
    @Override
    public String toString() {
        return toJsonString();
    }
}