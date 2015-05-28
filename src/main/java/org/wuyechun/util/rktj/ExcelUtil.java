package org.wuyechun.util.rktj;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.sql.TIMESTAMP;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelUtil {

	public static void main(String[] args) {

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		System.out.println("Begin time-"+sdf.format(new Date()));
		
		// 创建工作簿对象
		HSSFWorkbook hssfWorkBook = new HSSFWorkbook();
		// 创建 sheet 页
		HSSFSheet sheet = hssfWorkBook.createSheet();
		// 处理中文 sheet 页名称
		hssfWorkBook.setSheetName(0, "report");

		/**String sql = "select * from(\n"
				+ "select t.*,rownum as rn from psn_base_info t)\n"
				+ "where rn between 0 and 10000";
		***/
		
		String sql=
				"select b.* from (\n" +
						"SELECT\n" + 
						"PID,\n" + 
						"BOYS,\n" + 
						"GIRLS,\n" + 
						"HYRQ,\n" + 
						"MZCSRQ,\n" + 
						"MZXB,\n" + 
						"MZXB_NM,\n" + 
						"DQBYZK,\n" + 
						"DQBYZK_NM,\n" + 
						"DQBYRQ,\n" + 
						"MCYJJG,\n" + 
						"MCYJJG_NM,\n" + 
						"MCYJRQ,\n" + 
						"MCYJDEPT,\n" + 
						"MCHJJG,\n" + 
						"MCHJJG_NM,\n" + 
						"MCHJRQ,\n" + 
						"MCHJDEPT,\n" + 
						"MCSJJG,\n" + 
						"MCSJJG_NM,\n" + 
						"MCSJRQ,\n" + 
						"MCSJDEPT,\n" + 
						"ID,\n" + 
						"RYID,\n" + 
						"NAME,\n" + 
						"PY,\n" + 
						"XB,\n" + 
						"XB_NM,\n" + 
						"CSRQ,\n" + 
						"CHRQ,\n" + 
						"RKLX,\n" + 
						"RKLX_NM,\n" + 
						"HKXZ,\n" + 
						"HKXZ_NM,\n" + 
						"HYZK,\n" + 
						"HYZK_NM,\n" + 
						"IDCARD,\n" + 
						"PONAME,\n" + 
						"POPY,\n" + 
						"POCSRQ,\n" + 
						"POJHRQ,\n" + 
						"PORKLX,\n" + 
						"PORKLX_NM,\n" + 
						"POHKXZ,\n" + 
						"POHKXZ_NM,\n" + 
						"POHYZK,\n" + 
						"POHYZK_NM,\n" + 
						"POIDCARD,\n" + 
						"XXZZ,\n" + 
						"GLD,\n" + 
						"GLDDM,\n" + 
						"ZXYY,\n" + 
						"ZXYY_NM,\n" + 
						"ZXRQ,\n" + 
						"BZ,\n" + 
						"CJR,\n" + 
						"CJRQ,\n" + 
						"GXR,\n" + 
						"GXRQ,\n" + 
						"rownum as rn FROM PSN_JS_INOCULATION t,PSN_JS_PREGNANT s WHERE t.pid=s.id\n" + 
						"AND t.mzcsrq>to_date('2009-01-01','yyyy-mm-dd')\n" + 
						")b where b.rn between 0 and 50000";

		

		List resultList = doQueryByDbUtils(sql);
		
		boolean flag=false;
		
		if(flag){
			int m = 0;
			HSSFRow rowTitle = sheet.createRow(0);
			Map<String, Object> map = (Map<String, Object>) resultList.get(0);
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				// 创建单元格
				HSSFCell cell = rowTitle.createCell(m);
				// 为单元格填充值
				cell.setCellValue(entry.getKey() == null ? "" : entry.getKey().toString());
				m++;
			}

			for (int i = 0; i < resultList.size(); i++) {
				// 创建行
				HSSFRow row = sheet.createRow(i+1);
				
				Map<String, Object> dataMap = (Map<String, Object>) resultList.get(i);
				
				int j = 0;
				for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
					// System.out.println("key= " + entry.getKey() + "|| value= "+entry.getValue());
					// 创建单元格
					HSSFCell cell = row.createCell(j);
					// 为单元格填充值
					if(entry.getValue() instanceof TIMESTAMP) {
						try {
							cell.setCellValue(entry.getValue()==null?"":convertOrclTimestemp2String((TIMESTAMP)entry.getValue()));
						} catch (SQLException e) {
						}

					}else{
						cell.setCellValue(entry.getValue() == null ? "" : entry.getValue().toString());
					}
					j++;
				}
			}
		}else{
			//采用DbUtils查询
			for(int i=0;i<resultList.size();i++){
				HSSFRow row = sheet.createRow(i);
				Object[] obj=(Object[]) resultList.get(i);
				
				for(int j=0;j<obj.length;j++){
					HSSFCell cell = row.createCell(j);
					
					if(obj[j] instanceof TIMESTAMP) {
						try {
							cell.setCellValue(obj[j]==null?"":convertOrclTimestemp2String((TIMESTAMP) obj[j]));
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}else{
						cell.setCellValue(obj[j]==null?"":obj[j].toString());
					}
				}
			}
		}

		try {
			// 输出工作簿
			FileOutputStream fos = new FileOutputStream("F:\\Report.xls");
			// 将工作簿进行输出
			hssfWorkBook.write(fos);
			// 关闭输出流
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("End time-"+sdf.format(new Date()));
	}
	
	
	/**
	 * 
	 * 功能 :查询
	
	 * 开发：ycwu3 2015-4-28
	
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, Object>> doQuery(String sql) {

		String url = "jdbc:oracle:thin:@172.16.10.41:1521:center";
		String user = "xftest";
		String password = "123456";

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Connection conn =null;
		ResultSet rs =null;
		PreparedStatement pstm =null;
		
		try {
			conn = DbConnUtil.getDbConn(url, user, password);
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					map.put(md.getColumnName(i), rs.getObject(i));
				}
				list.add(map);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
			if (pstm != null) {
				try {
					pstm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	
	/**
	 * 
	 * 功能 :通过DbUtils查询
	
	 * 开发：wuyechun 2015-5-19
	
	 * @param sql
	 * @return
	 */
	public static List<Object[]> doQueryByDbUtils(String sql){

		//String url = "jdbc:oracle:thin:@172.16.10.41:1521:center";
		//String user = "xftest";
		//String password = "123456";
		
		String url = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=172.16.7.114)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=172.16.7.116)(PORT=1521))(LOAD_BALANCE=yes)(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=center)(FAILOVER_MODE=(TYPE=SELECT)(METHOD=BASIC)(RETRIES=180)(DELAY=5))))";
		String user="center"; 
		String password="123456";
		
		
		Connection conn = DbConnUtil.getDbConn(url, user, password);
		ResultSetHandler<List<Object[]>> rsh = new ArrayListHandler();
		QueryRunner queryRunner = new QueryRunner();  
		List<Object[]> list=new ArrayList<Object[]>();
		try {
			list =queryRunner.query(conn, sql,rsh);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	} 
	
	/**
	 * 
	 * 功能 :将timpstamp 转换成正常显示的时间串
	
	 * 开发：wuyechun 2015-5-19
	
	 * @param temp
	 * @return
	 * @throws SQLException
	 */
	public static String convertOrclTimestemp2String(oracle.sql.TIMESTAMP temp) throws SQLException {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
		java.sql.Timestamp tt = (java.sql.Timestamp)temp.toJdbc();
		return sdf.format(new Date(tt.getTime()));
    }
	
	//Invalid row number (65536) outside allowable range (0..65535)
	//HSSFRow.java:239
	//-Xms512M -Xmx1024M     VM arguments
}
