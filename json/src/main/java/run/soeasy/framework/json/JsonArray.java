package run.soeasy.framework.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * JSON数组实现类，继承自{@link ArrayList}并实现{@link JsonElement}接口，
 * 用于表示JSON格式中的数组结构，存储多个{@link JsonElement}元素（可包含对象、数组、基本类型等），
 * 支持将数组内容序列化为符合JSON语法的字符串。
 * 
 * <p>该类通过{@link #export(Appendable)}方法实现JSON数组的序列化，自动处理元素间的逗号分隔，
 * 并添加数组的起始符'['和结束符']'，确保输出格式符合JSON规范。
 * 
 * @author soeasy.run
 * @see JsonElement
 * @see ArrayList
 */
public class JsonArray extends ArrayList<JsonElement> implements JsonElement {

    /**
     * 序列化版本号，确保集合在序列化与反序列化过程中的兼容性
     */
    private static final long serialVersionUID = 1L;

    /**
     * JSON数组的起始符号（固定为'['）
     */
    public static final char PREFIX = '[';

    /**
     * JSON数组的结束符号（固定为']'）
     */
    public static final char SUFFIX = ']';

    /**
     * 将JSON数组导出为符合JSON语法的字符串
     * 
     * <p>处理流程：
     * 1. 输出数组起始符'['；
     * 2. 遍历数组中的每个{@link JsonElement}，调用其{@link JsonElement#export(Appendable)}方法导出元素；
     * 3. 元素间添加逗号和空格（", "）作为分隔符；
     * 4. 输出数组结束符']'。
     * 
     * @param target 接收导出数据的 Appendable（如StringBuilder、Writer等）
     * @throws IOException 当Appendable操作失败时抛出（如IO写入错误）
     */
    @Override
    public void export(Appendable target) throws IOException {
        // 输出数组起始符
        target.append(PREFIX);
        // 遍历数组元素
        Iterator<JsonElement> iterator = iterator();
        while (iterator.hasNext()) {
            // 导出当前元素
            iterator.next().export(target);
            // 若存在下一个元素，添加逗号和空格作为分隔
            if (iterator.hasNext()) {
                target.append(',');
                target.append(' ');
            }
        }
        // 输出数组结束符
        target.append(SUFFIX);
    }

    /**
     * 返回JSON数组的字符串表示（即序列化后的JSON格式字符串）
     * 
     * @return 符合JSON规范的数组字符串
     */
    @Override
    public String toString() {
        return toJsonString();
    }
}