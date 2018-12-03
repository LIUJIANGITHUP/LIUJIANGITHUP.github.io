package cn.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JOptionPane;
//公司qc数据库连接
public class JDBCUtils {
	private static String driver;
	private static String url;
	private static String user;
	private static String password;
     static {
			try {
				// 读取配置文件中数据库连接所需的驱动用户名密码等
				Properties props = new Properties();
				ClassLoader classLoader = JDBCUtils.class.getClassLoader();// 读取属性文件xxxxx.properties
	            InputStream is = classLoader.getResourceAsStream("database.properties");
				props.load(is);
				// 使用getProperty(key)，通过key获得需要的值，
				driver = props.getProperty("driver");
				url = props.getProperty("url");
				user = props.getProperty("user");
				password = props.getProperty("password");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * 获得连接
		 */
		public static Connection getConnection() {
			try {
				// 1 注册驱动
				Class.forName(driver);
				// 2 获得连接
				Connection conn = DriverManager.getConnection(url, user, password);
				return conn;
			} catch (Exception e) {
				 JOptionPane.showMessageDialog(null, e+"数据库连接地址错误或不能远程操作内网数据库");
				 throw new RuntimeException(e);
			}
		}
		/*
		 * 关闭资源
		 */
		public static void closeAll(PreparedStatement per,Connection conn) {
			
			//关闭执行对象
			if(per!=null){
			try {
			per.close();
			} catch (SQLException e) { 
			e.printStackTrace();
			}
			}
			//关闭执行对象
			if(conn!=null){
			try {
			conn.close();
			} catch (SQLException e) { 
			e.printStackTrace();
			}
			}
			}
		}

