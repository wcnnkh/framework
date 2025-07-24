package run.soeasy.framework.json;

import java.io.IOException;

/**
 * JSON空值类型实现类，实现{@link JsonElement}接口，用于表示JSON中的"null"值，
 * 采用单例模式确保全局唯一的null值实例，避免不必要的对象创建。
 * 
 * <p>该类通过重写{@link #export(Appendable)}方法输出"null"字符串，
 * 并统一JSON null值的序列化行为，确保在JSON处理中null值的一致性。
 * 
 * @author soeasy.run
 * @see JsonElement
 */
public class JsonNull implements JsonElement {

    /**
     * JSON null值的单例实例，全局唯一
     */
    public static final JsonNull INSTANCE = new JsonNull();

    /**
     * 私有构造方法，确保只能通过{@link #INSTANCE}获取实例（单例模式）
     */
    private JsonNull() {
    }

    /**
     * 将JSON null值导出为"null"字符串
     * 
     * @param target 接收导出数据的Appendable（如StringBuilder、Writer等）
     * @throws IOException 当Appendable操作失败时抛出（如IO错误）
     */
    @Override
    public void export(Appendable target) throws IOException {
        target.append("null");
    }

    /**
     * 返回JSON null值的字符串表示（即"null"）
     * 
     * @return "null"字符串
     */
    @Override
    public String toString() {
        return toJsonString();
    }
}