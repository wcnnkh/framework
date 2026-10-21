package run.soeasy.framework.jdbc;

import java.io.Serializable;
import java.util.Arrays;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.type.ClassUtils;

/**
 * SQL语句与参数封装类，用于统一管理SQL字符串及其对应的参数数组，
 * 提供SQL格式化显示、参数与占位符校验、SQL片段截取等功能，方便SQL的构建、调试与处理。
 * 
 * <p>该类支持将SQL中的占位符（?）与实际参数关联，可生成带实际参数的SQL字符串（便于调试），
 * 并验证参数数量与占位符数量的一致性，避免SQL执行时的参数不匹配问题。
 * 
 * @author soeasy.run
 */
@Data
public class Sql implements Serializable {

    /**
     * SQL参数占位符常量，对应标准SQL中的"?"
     */
    private static final String PARAMETER_PLACEHOLDER = "?";

    /**
     * 序列化版本号，确保对象序列化与反序列化的兼容性
     */
    private static final long serialVersionUID = 1L;

    /**
     * SQL语句字符串（包含参数占位符"?"）
     */
    @NonNull
    private final String statement;

    /**
     * SQL语句对应的参数数组，与statement中的"?"一一对应
     */
    @NonNull
    private final Object[] args;

    /**
     * 构造SQL对象（指定SQL语句和参数数组）
     * 
     * @param statement SQL语句字符串（非空，可包含"?"占位符）
     * @param args SQL参数数组（非空，与占位符数量需匹配，否则验证会失败）
     */
    public Sql(@NonNull String statement, @NonNull Object... args) {
        this.statement = statement;
        this.args = args;
    }

    /**
     * 生成可直接执行的SQL字符串（替换占位符为实际参数）
     * 
     * <p>处理逻辑：
     * 1. 遍历参数数组，依次替换SQL中的"?"占位符；
     * 2. 对null值显示为"null"；
     * 3. 基本类型（非字符型）直接显示值；
     * 4. 其他类型（如字符串）添加单引号包裹，并转义单引号（避免SQL语法错误）。
     * 
     * @return 替换占位符后的SQL字符串（可用于调试或直接执行）
     */
    public String display() {
        StringBuilder sb = new StringBuilder();
        int lastFind = 0; // 记录上一次找到占位符的位置
        for (int i = 0; i < args.length; i++) {
            // 从上次位置开始查找下一个占位符
            int index = statement.indexOf(PARAMETER_PLACEHOLDER, lastFind);
            if (index == -1) {
                // 未找到更多占位符，终止循环
                break;
            }

            // 拼接上次位置到当前占位符前的SQL片段
            sb.append(statement.substring(lastFind, index));
            Object param = args[i];

            // 根据参数类型处理显示格式
            if (param == null) {
                sb.append("null");
            } else {
                Class<?> paramClass = param.getClass();
                if (ClassUtils.isPrimitiveOrWrapper(paramClass) && !ClassUtils.isChar(paramClass)) {
                    // 基本类型（非字符型）直接拼接值
                    sb.append(param);
                } else {
                    // 其他类型（如字符串）添加单引号并转义内部单引号
                    sb.append("'")
                      .append(StringUtils.transferredMeaning(String.valueOf(param), '\''))
                      .append("'");
                }
            }

            // 更新上次查找位置为当前占位符后一位
            lastFind = index + 1;
        }

        // 拼接剩余的SQL片段（未替换的部分）
        if (lastFind == 0) {
            // 未找到任何占位符，直接拼接完整SQL
            sb.append(statement);
        } else {
            sb.append(statement.substring(lastFind));
        }

        return sb.toString();
    }

    /**
     * 返回SQL语句与参数的简要描述（用于日志或调试）
     * 
     * <p>格式：
     * - 无参数：直接返回SQL语句
     * - 有参数：[SQL语句] - [参数数组toString()]
     * 
     * @return SQL与参数的字符串表示
     */
    @Override
    public String toString() {
        if (args == null || args.length == 0) {
            return statement;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(statement).append("]");
            sb.append(" - ").append(Arrays.toString(args));
            return sb.toString();
        }
    }

    /**
     * 判断SQL是否无效（参数数量与占位符数量不一致）
     * 
     * <p><strong>注意</strong>：返回true表示无效（参数与占位符数量不匹配），返回false表示有效。
     * 
     * @return 有效（数量一致）返回false，无效（数量不一致）返回true
     */
    public boolean isValid() {
        // 当参数数量与占位符数量不等时，视为无效
        return args.length != StringUtils.count(statement, PARAMETER_PLACEHOLDER);
    }

    /**
     * 验证参数数量与占位符数量是否一致，不一致则抛出异常
     * 
     * @throws IllegalStateException 当参数数量与占位符数量不匹配时抛出
     */
    public void verification() throws IllegalStateException {
        if (isValid()) { // 注意：isValid()为true表示无效
            throw new IllegalStateException(
                "The number of parameter placeholders is inconsistent with the number of parameters <" + toString() + ">"
            );
        }
    }

    /**
     * 截取SQL的指定片段，并提取对应的参数，生成新的Sql对象
     * 
     * <p>逻辑：
     * 1. 先验证原SQL的有效性（参数与占位符数量一致）；
     * 2. 截取SQL字符串从start到end的子串；
     * 3. 计算截取片段中的占位符数量，提取原参数数组中对应的部分；
     * 4. 返回包含截取片段和对应参数的新Sql对象。
     * 
     * @param start 截取起始索引（包含）
     * @param end 截取结束索引（不包含）
     * @return 截取后的Sql对象
     */
    public Sql sub(int start, int end) {
        verification(); // 先验证原SQL有效

        String targetSql = statement.substring(start, end);
        Object[] targetParams;

        if (args.length == 0) {
            targetParams = new Object[0];
        } else {
            // 计算截取片段中的占位符数量
            int segmentPlaceholderCount = StringUtils.count(statement, start, end, PARAMETER_PLACEHOLDER);
            // 计算原参数数组中对应截取片段的起始索引
            int paramStartIndex = StringUtils.count(statement, 0, start, PARAMETER_PLACEHOLDER);

            // 提取对应参数
            targetParams = new Object[segmentPlaceholderCount];
            for (int i = paramStartIndex, targetIndex = 0; i < paramStartIndex + segmentPlaceholderCount; i++, targetIndex++) {
                targetParams[targetIndex] = args[i];
            }
        }

        return new Sql(targetSql, targetParams);
    }
}