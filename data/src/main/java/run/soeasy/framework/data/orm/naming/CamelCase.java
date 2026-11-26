package run.soeasy.framework.data.orm.naming;

import lombok.NonNull;

public class CamelCase implements NamingStrategy {
	public static final CamelCase INSTANCE = new CamelCase();
	
    /**
     * 蛇形命名（下划线分隔）转小驼峰 camelCase
     * 对齐 MyBatis UnderscoreToCamelCase 标准行为
     * 支持脏输入：首尾下划线、连续下划线、输入自带大写
     * <p>
     * example:
     * user_name       → userName
     * _user_id_       → userId
     * order__no       → orderNo
     * USER_INFO       → userInfo
     * </p>
     */
    @Override
    public String apply(@NonNull String name) {
        int len = name.length();
        if (len == 0) {
            return name;
        }
        StringBuilder sb = new StringBuilder(len);
        boolean upperNext = false;

        for (int i = 0; i < len; i++) {
            char c = name.charAt(i);
            if (c == '_') {
                upperNext = true;
                continue;
            }
            if (upperNext) {
                sb.append(Character.toUpperCase(c));
                upperNext = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }
}
