package com.yzw.jmetersampler.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzw.jmetersampler.config.DBConfig;

import java.sql.*;


public class MySqlService {
    private Connection connection;

    public MySqlService(DBConfig dbConfig) throws SQLException {
        String url = dbConfig.DbHost;
        String username = dbConfig.DbUsername;
        String password = dbConfig.DbPassword;
        this.connection = DriverManager.getConnection(url, username, password);
    }


    private void release(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            rs = null;
        }
        if (st != null) {
            try {
                st.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean insert(String sql) {
//        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
//            conn = getConnection();
            st = this.connection.createStatement();
            boolean num = st.execute(sql);
            if (num = true) {
//                System.out.println("添加数据成功");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            release(connection, st, rs);
        }
        return true;
    }

    public boolean delete(String sql) {
//        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
//            conn = getConnection();
            st = this.connection.createStatement();
            int num = st.executeUpdate(sql);
            if (num > 0) {
//                System.out.println("删除数据成功");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            release(connection, st, rs);
        }
        return true;
    }

    public int update(String sql) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
//            conn = getConnection();
            st = this.connection.createStatement();
            int num = st.executeUpdate(sql);
            if (num >= 1) {
//                System.out.println("修改数据成功");
                return num;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            release(conn, st, rs);
        }
        return 0;
    }

    public JSONArray query(String sql) {
//        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        JSONArray array = new JSONArray();
        try {
//            conn = getConnection();
            st = this.connection.createStatement();
            rs = st.executeQuery(sql);
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = md.getColumnLabel(i);
                    String value = rs.getString(columnName);
                    jsonObject.put(columnName, value);
                }
                array.add(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return array;
        } finally {
            release(connection, st, rs);
        }
//        System.out.println(array);
        return array;
    }

    public Object executeSql(String sql){
        sql = sql.trim().toLowerCase();
        String[] s = sql.trim().split(" ");
        Object result = null;
        if (s.length > 2){
            switch (s[0]) {
                case "select":
                    result = query(sql);
                    break;
                case "update":
                    result = update(sql);
                    break;
                case "delete":
                    result =delete(sql);
                    break;
                case "insert":
                    result =insert(sql);
                    break;
                default:
                    System.out.println("sql错误");
            }
        }
        return result;

    }

    public static void main(String[] args) throws SQLException {
//        DBConfig dbConfig = new DBConfig();
//        dbConfig.setDbHost("jdbc:mysql://172.16.0.130:8301/yz_mp_ums");
//        dbConfig.setDbPassword("YZ.zk.owner@2019");
//        dbConfig.setDbUsername("app_owner");
//        MySqlService mySqlService = new MySqlService(dbConfig);
//        System.out.println(        mySqlService.query("select count(*) from user"));
    }
}
