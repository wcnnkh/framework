package run.soeasy.framework.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;

/**
 * 简单的数据库连接工厂，实现{@link ConnectionFactory}接口，基于{@link DriverManager}创建数据库连接，
 * 适用于基础的JDBC连接场景（非连接池模式），支持通过URL、用户名/密码或属性集合（{@link Properties}）配置连接参数，
 * 提供简单直接的数据库连接获取方式。
 * 
 * <p>该类通过{@link DriverManager#getConnection(String, Properties)}获取连接，不涉及连接池管理，
 * 每次调用{@link #getConnection()}都会创建新的连接，适合简单应用或测试环境，不建议在高并发场景中使用。
 * 
 * @author soeasy.run
 * @see ConnectionFactory
 * @see DriverManager
 * @see Connection
 */
@Data
public class SimpleConnectionFactory implements ConnectionFactory {

    /**
     * Properties中存储用户名的键（固定为"user"，与JDBC规范一致）
     */
    public static final String USER_KEY = "user";

    /**
     * Properties中存储密码的键（固定为"password"，与JDBC规范一致）
     */
    public static final String PASSWORD_KEY = "password";

    /**
     * 数据库连接URL（如"jdbc:mysql://localhost:3306/test"，非空）
     */
    @NonNull
    private final String url;

    /**
     * 连接属性集合，包含用户名、密码等连接参数（可为null，若为null则使用默认配置）
     */
    private final Properties info;

    /**
     * 通过数据库URL、用户名和密码构造连接工厂
     * 
     * <p>处理逻辑：
     * 1. 若用户名为非空字符串，将其存入info的{@value #USER_KEY}键；
     * 2. 若密码为非空字符串，将其存入info的{@value #PASSWORD_KEY}键；
     * 3. 最终通过{@link DriverManager}使用url和构建的info获取连接。
     * 
     * @param url 数据库连接URL（非空，如"jdbc:postgresql://host:port/dbname"）
     * @param user 数据库用户名（可为null，若为null则不设置用户信息）
     * @param password 数据库密码（可为null，若为null则不设置密码信息）
     */
    public SimpleConnectionFactory(@NonNull String url, String user, String password) {
        this.url = url;
        Properties info = null;
        // 处理用户名
        if (StringUtils.isNotEmpty(user)) {
            info = new Properties();
            info.put(USER_KEY, user);
        }
        // 处理密码
        if (StringUtils.isNotEmpty(password)) {
            if (info == null) {
                info = new Properties();
            }
            info.put(PASSWORD_KEY, password);
        }
        this.info = info;
    }

    /**
     * 通过数据库URL和属性集合构造连接工厂
     * 
     * <p>属性集合{@code info}可包含连接所需的所有参数（如"user"、"password"、"characterEncoding"等），
     * 直接传递给{@link DriverManager#getConnection(String, Properties)}使用。
     * 
     * @param url 数据库连接URL（非空）
     * @param info 包含连接参数的Properties（可为null，若为null则使用默认连接参数）
     */
    public SimpleConnectionFactory(@NonNull String url, Properties info) {
        this.url = url;
        this.info = info;
    }

    /**
     * 通过{@link DriverManager}获取数据库连接
     * 
     * <p>调用{@link DriverManager#getConnection(String, Properties)}，使用当前的url和info参数创建连接，
     * 具体连接行为由底层JDBC驱动实现（需确保驱动已加载到类路径）。
     * 
     * @return 新的数据库连接实例（需在使用后手动关闭，或通过{@link ConnectionWrapped}自动管理）
     * @throws SQLException 当获取连接失败时抛出（如URL无效、驱动未找到、认证失败等）
     */
    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, info);
    }
}