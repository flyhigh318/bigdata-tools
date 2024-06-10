package com.devops.check.hadoop.jdbc.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JdbcSql {

    private String connectionUrl = "jdbc:hive2://localhost:10000/default";
    private String driverName = "org.apache.hive.jdbc.HiveDriver";
    private String sql = "show databases";

    public JdbcSql() {
    }
    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }
    public String getConnectionUrl() {
        return connectionUrl;
    }
    public void setSql(String sql) {
        this.sql = sql;
    }
    public String getSql() {
        return sql;
    }
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    public String getDriverName() {
        return driverName;
    }
    public  void excuteSql() {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = DriverManager.getConnection(connectionUrl);
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}