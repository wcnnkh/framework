package run.soeasy.framework.json;

import java.io.IOException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.Value;
import run.soeasy.framework.core.math.BigDecimalValue;
import run.soeasy.framework.core.math.IntValue;
import run.soeasy.framework.core.math.LongValue;
import run.soeasy.framework.core.math.NumberValue;

/**
 * JSON基本类型实现类，实现{@link JsonElement}和{@link Value}接口，用于表示JSON中的基本数据类型，
 * 包括字符串、数字（整数、长整数、浮点数等）、布尔值等，封装了基本类型与JSON格式的转换逻辑。
 * 
 * <p>该类通过包装Java基本类型或{@link NumberValue}对象，提供了JSON序列化和值访问的统一接口，
 * 确保基本类型在JSON处理中能够正确地转换为对应的字符串表示。
 * 
 * @author soeasy.run
 * @see JsonElement
 * @see Value
 * @see NumberValue
 */
@RequiredArgsConstructor
public class JsonPrimitive implements JsonElement, Value {

    /**
     * 包装的原始值（非空，可为String、Number、Boolean等基本类型或{@link NumberValue}）
     */
    @NonNull
    private final Object value;

    /**
     * 将JSON基本类型导出为符合JSON语法的字符串
     * 
     * <p>处理规则：
     * <ul>
     * <li>字符串类型：添加双引号包裹，并对特殊字符进行转义；
     * <li>其他类型（数字、布尔值等）：直接输出其字符串表示（无需引号）。
     * </ul>
     * 
     * @param target 接收导出数据的Appendable（如StringBuilder、Writer等）
     * @throws IOException 当Appendable操作失败时抛出（如IO写入错误）
     */
    @Override
    public void export(Appendable target) throws IOException {
        String str = JsonElement.escaping(getAsString());
        if (this.value instanceof String) {
            // 字符串类型需用双引号包裹
            target.append('"');
            target.append(str);
            target.append('"');
        } else {
            // 非字符串类型直接输出
            target.append(str);
        }
    }

    /**
     * 将当前值转换为{@link NumberValue}（数字值包装类）
     * 
     * <p>转换规则：
     * <ul>
     * <li>若原始值已是{@link NumberValue}，直接返回；
     * <li>若原始值是{@link Long}，转换为{@link LongValue}；
     * <li>若原始值是{@link Integer}，转换为{@link IntValue}；
     * <li>其他情况（如Float、Double等），转换为{@link BigDecimalValue}。
     * </ul>
     * 
     * @return 对应的{@link NumberValue}实例，若原始值为null则返回null
     */
    @Override
    public NumberValue getAsNumber() {
        if (value instanceof NumberValue) {
            return (NumberValue) value;
        }

        if (value instanceof Long) {
            return new LongValue((long) value);
        }

        if (value instanceof Integer) {
            return new IntValue((int) value);
        }

        return value == null ? null : new BigDecimalValue(String.valueOf(value));
    }

    /**
     * 获取当前值的字符串表示
     * 
     * <p>若原始值是{@link NumberValue}，则调用其{@link NumberValue#getAsString()}方法；
     * 其他类型直接通过{@link String#valueOf(Object)}转换为字符串。
     * 
     * @return 当前值的字符串表示
     */
    @Override
    public String getAsString() {
        if (value instanceof NumberValue) {
            return ((NumberValue) value).getAsString();
        }
        return String.valueOf(value);
    }

    /**
     * 判断当前值是否为数字类型
     * 
     * @return 若原始值是{@link Number}的实例（包括包装类型），则返回true，否则返回false
     */
    @Override
    public boolean isNumber() {
        return value instanceof Number;
    }

    /**
     * 返回当前JSON基本类型的字符串表示（即序列化后的JSON格式字符串）
     * 
     * @return 符合JSON规范的基本类型字符串
     */
    @Override
    public String toString() {
        return toJsonString();
    }
}