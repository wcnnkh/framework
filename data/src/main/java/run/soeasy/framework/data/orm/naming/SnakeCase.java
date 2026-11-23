package run.soeasy.framework.data.orm.naming;

import lombok.NonNull;

public class SnakeCase implements NamingStrategy {
	public static final SnakeCase SNAKE_CASE = new SnakeCase();

    /**
     * 小驼峰 → 下划线蛇形命名
     * 用于SQL生成阶段：实体属性名转数据库字段名
     * 单向转换，不与CamelCase绑定，职责独立
     *
     * <p>示例：
     * userName    → user_name
     * orderId     → order_id
     * httpApi     → http_api
     * userIdList  → user_id_list
     * </p>
     */
    @Override
    public String apply(@NonNull String name) {
        int len = name.length();
        if (len == 0) {
            return name;
        }
        StringBuilder sb = new StringBuilder(len + 4);
        for (int i = 0; i < len; i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
