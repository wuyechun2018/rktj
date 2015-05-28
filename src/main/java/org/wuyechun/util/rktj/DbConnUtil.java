package org.wuyechun.util.rktj;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbConnUtil {

	/**
	 * 
	 * 功能 :获取数据库连接
	
	 * 开发：ycwu3 2015-4-28
	
	 * @return
	 */
	public static Connection getDbConn(String url,String user,String password){
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Connection conn=null;
		try {
			conn = DriverManager.getConnection(url,user,password);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return conn;
	}
	
	
	
	/**
	 * 
	 * 功能 :查询
	
	 * 开发：ycwu3 2015-4-28
	
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static int doQuery(String sql) {
		
		String url = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=172.16.7.114)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=172.16.7.116)(PORT=1521))(LOAD_BALANCE=yes)(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=center)(FAILOVER_MODE=(TYPE=SELECT)(METHOD=BASIC)(RETRIES=180)(DELAY=5))))";
		String user="center"; 
		String password="123456";
		
		int count=0;
		try {
			Connection  conn=DbConnUtil.getDbConn(url,user,password);
			PreparedStatement pstm =conn.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
		
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
			//关闭连接
			if (pstm != null) {
				pstm.close();
			}
			if(conn!=null){
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	
    //String url = "jdbc:oracle:thin:@localhost:1521:orcl";
	//String user="dms"; 
	//String password="dms";
	//sql="SELECT COUNT(1) FROM SQL_BOOK";
	
}
