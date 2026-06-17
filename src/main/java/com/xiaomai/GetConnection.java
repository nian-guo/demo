/**package com.xiaomai;

import java.sql.Connection;
import java.sql.DriverManager;

public class GetConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/wheat_trace?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai";
    private static final String USER = "root";
    private static final String PASSWORD = "267610";   // 改成你自己的密码！！！

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("===== 数据库连接失败 =====");
            e.printStackTrace();
            return null;
        }
    }
}
*/