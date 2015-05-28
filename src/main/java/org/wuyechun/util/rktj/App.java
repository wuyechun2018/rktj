package org.wuyechun.util.rktj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

/**
 * Hello world!
 * 
 */
public class App {
	
	public static void main(String[] args) {
		fltj();
	}
	
	
	/**
	 * 
	 * 功能 :分类统计
	
	 * 开发：ycwu3 2015-5-14
	 */
	public static void fltj(){
		
		String [] areaName={"镜湖区","弋江区","三山区","鸠江区","经开区","无为县","繁昌县","南陵县","芜湖县"};
		String [] areaCode={"340202","340203","340208","340207","340217","340225","340222","340223","340221"};
		
		//MessageFormat有  ' 需要转义成 ''  即用2个单引号进行转义
		
		//没有登记户籍的人口
		String sqlWHJ="select count(1) from\n" +
						"psn_person s,loc_house m\n" + 
						"where s.house_id=m.id\n" + 
						"and s.logoff_flag=''0''\n" + 
						"and s.idcard not in(\n" + 
						"select s.idcard from  psn_census_info s where s.log_off_flag=''0''\n" + 
						")\n" + 
						"and s.idcard like ''3402%''\n" + 
						"and m.building_no like ''{0}%''";
		
		//军人
		String sqlJR="select count(1) from\n" +
						"psn_person s,loc_house m\n" + 
						"where s.house_id=m.id\n" + 
						"and s.logoff_flag=''0''\n" + 
						"and s.certificate=''3''\n" + 
						"and m.building_no like ''{0}%''";

		
		//所有外国人
		String sqlWGR="select count(1) from\n" +
						"psn_person s,loc_house m\n" + 
						"where s.house_id=m.id\n" + 
						"and s.logoff_flag=''0''\n" + 
						"and s.certificate=''2''\n" + 
						"and m.building_no like ''{0}%''";

		
		
		//港澳台同胞
		String sqlGAT="select count(1) from\n" +
						"psn_person s,loc_house m\n" + 
						"where s.house_id=m.id\n" + 
						"and s.logoff_flag=''0''\n" + 
						"and s.certificate=''2''\n" + 
						"and m.building_no like ''{0}%''\n" + 
						"and (s.census_district_nm like ''%香港%''  or  s.census_district_nm like ''%台湾%''  or  s.census_district_nm like ''%澳门%'')";
		
		for(int i=0;i<areaCode.length;i++){
			System.out.println("=================================================="+areaName[i]);
			String whj=MessageFormat.format(sqlWHJ,areaCode[i]);
			//System.out.println(whj);
			int countWHJ=doQuery(whj);
			System.out.println("无户籍========================="+countWHJ);
			//System.out.println(whj);
			
			
			String jr=MessageFormat.format(sqlJR,areaCode[i]);
			int countJR=doQuery(jr);
			System.out.println("军人========================="+countJR);
			//System.out.println(jr);
			
			String wgr=MessageFormat.format(sqlWGR,areaCode[i]);
			int countWGR=doQuery(wgr);
			System.out.println("所有外国人========================="+countWGR);
			//System.out.println(wgr);
			
			String gat=MessageFormat.format(sqlGAT,areaCode[i]);
			int countGAT=doQuery(gat);
			System.out.println("港澳台同胞========================="+countGAT);

			System.out.println("外国人(除 港澳台)========================="+(countWGR-countGAT));
			//System.out.println(gat);
		}
	}
	

	
	/**
	 * 
	 * 功能 :详细人口信息统计
	
	 * 开发：ycwu 2015-5-14
	 */
	public void xxtj(){
		//区域编码
		String [] areaName={"镜湖区","弋江区","三山区","鸠江区","经开区","无为县","繁昌县","南陵县","芜湖县"};
		String [] areaCode={"340202","340203","340208","340207","340217","340225","340222","340223","340221"};
		
		
		String sql2="SELECT count(1) FROM(\n" +
				"   SELECT t.idcard,t.census_district,t.actual_district,t.census_type,t.census_state\n" + 
				"  FROM psn_person t\n" + 
				" WHERE logoff_flag = '0'\n" + 
				"   AND t.census_state <>'3'\n" + 
				"   AND  t.building_no like '3402%')s\n" + 
				"   WHERE s.idcard not in(\n" + 
				"         select m.idcard  from psn_census_info m where m.log_off_flag='0'\n" + 
				"   )\n";

		String temp="";
		
		for(int m=0;m<areaCode.length;m++){
			System.out.println("-------------外来人口----------"+m+"-"+areaName[m]+"----------");
			temp=" AND s.actual_district='"+areaCode[m]+"'";
			//System.out.println(sql2+temp);
			
			int num=doQuery(sql2+temp);
			System.out.println(num);
			
			temp="";
		}
		
		System.out.println("====================================================");
		
		String sql="select count(1) from\n" +
						"psn_census_info t,psn_person s\n" + 
						"where t.idcard=s.idcard\n" + 
						"AND s.building_no  like '3402%'\n" + 
						"and s.logoff_flag='0'\n" + 
						"and t.log_off_flag='0'\n"+
						//"and t.census_district='340202'\n" + 
						//"and s.actual_district='340202'\n" + 
						"and s.census_state<>'3'\n";
		
		
		String sqlPart="";
		int count=0;
		
		for(int i=0;i<areaCode.length;i++){
			sqlPart="and t.census_district='"+areaCode[i]+"'\n";
			
			for(int j=0;j<areaCode.length;j++){
				count++;
				
				sqlPart+="and s.actual_district='"+areaCode[j]+"'\n";
				
				System.out.println("-------------"+count+"----------"+areaName[i]+"-->"+areaName[j]);
				//System.out.println(sql+sqlPart);
				int num=doQuery(sql+sqlPart);
				System.out.println(num);
				
				sqlPart="and s.census_district='"+areaCode[i]+"'\n";
			}
		}
		
	}
	
	/***
	 * 
	 * 功能 :脱管人口统计
	
	 * 开发：ycwu3 2015-5-27
	 */
	public void tgrk(){
		
		//区域编码
		String [] areaName={"镜湖区","弋江区","三山区","鸠江区","经开区","无为县","繁昌县","南陵县","芜湖县"};
		String [] areaCode={"340202","340203","340208","340207","340217","340225","340222","340223","340221"};
		
		String idcardSql=
				"SELECT COUNT(1) FROM\n" +
						"(\n" + 
						"SELECT m.*, n.idcard as hz_idcard\n" + 
						"  FROM (\n" + 
						"        --通过IDCARD关联\n" + 
						"        SELECT t.idcard, t.name, t.sex, t.birthday, s.census_district,s.census_no\n" + 
						"          FROM PSN_BASE_INFO t, PSN_CENSUS_INFO s\n" + 
						"         WHERE t.idcard = s.idcard\n" + 
						"           AND t.logoff_flag = '0'\n" + 
						"           AND s.log_off_flag = '0'\n" + 
						"           AND t.house_id is null\n" + 
						"           --AND to_date(t.birthday, 'yyyy-mm-dd') between\n" + 
						"           --   to_date('2009-01-01', 'yyyy-mm-dd') AND\n" + 
						"           --   to_date('2015-05-22', 'yyyy-mm-dd')\n" + 
						"\n" + 
						") m\n" + 
						"  left join (select t.idcard,\n" + 
						"                    t.census_district,\n" + 
						"                    t.census_no,\n" + 
						"                    t.relation,\n" + 
						"                    ROW_NUMBER() over(PARTITION by census_no order by census_no desc) as rn\n" + 
						"               from PSN_CENSUS_INFO t\n" + 
						"               WHERE relation='02'\n" + 
						"               ) n\n" + 
						"    on (m.census_no = n.census_no AND m.census_district=n.census_district)\n" + 
						"AND n.rn=1) Q,PSN_BASE_INFO V\n" + 
						"WHERE Q.hz_idcard=v.idcard\n" + 
						"AND v.house_id is not null\n";
						//"AND v.actual_district='340221'";

		String namesexbirthdaySql="SELECT COUNT(1) FROM\n" +
						"(\n" + 
						"SELECT m.*, n.idcard as hz_idcard\n" + 
						"  FROM (\n" + 
						"\n" + 
						"--通过姓名，性别，出生日期关联\n" + 
						"SELECT t.idcard,t.name,t.sex,t.birthday,s.census_no,s.census_district FROM PSN_BASE_INFO t,PSN_CENSUS_INFO s\n" + 
						" WHERE\n" + 
						" t.idcard<>s.idcard\n" + 
						"AND t.name=s.name\n" + 
						"AND t.sex=s.sex\n" + 
						"--AND to_date(t.birthday,'yyyy-mm-dd')=to_date(s.birthday,'yyyy-mm-dd')\n" + 
						"AND to_char(to_date(t.birthday,'yyyy-mm-dd'), 'yyyymmdd')=s.birthday\n" + 
						"AND t.logoff_flag='0'\n" + 
						"AND s.log_off_flag='0'\n" + 
						"AND t.house_id is null\n" + 
						"--AND to_date(t.birthday,'yyyy-mm-dd') between to_date('2009-01-01','yyyy-mm-dd')\n" + 
						"--AND to_date('2015-05-22','yyyy-mm-dd')\n" + 
						") m\n" + 
						"  left join (select t.idcard,\n" + 
						"                    t.census_district,\n" + 
						"                    t.census_no,\n" + 
						"                    t.relation,\n" + 
						"                    ROW_NUMBER() over(PARTITION by census_no order by census_no desc) as rn\n" + 
						"               from PSN_CENSUS_INFO t\n" + 
						"               WHERE relation='02'\n" + 
						"               ) n\n" + 
						"    on (m.census_no = n.census_no AND m.census_district=n.census_district)\n" + 
						"AND n.rn=1) Q,PSN_BASE_INFO V\n" + 
						"WHERE Q.hz_idcard=v.idcard\n" + 
						"AND v.house_id is not null\n" ;
						//"AND v.actual_district='340221'";

		String sqlPart="";
		for(int i=0;i<areaCode.length;i++){
			sqlPart="AND v.actual_district='"+areaCode[i]+"'\n";
			
			int idCardNum=doQuery(idcardSql+sqlPart);
			int namesexbirthdayNum=doQuery(idcardSql+namesexbirthdaySql);
			
			System.out.println(areaName[i]+"根据IDCARD关联："+idCardNum+"根据name,sex,birthday关联"+namesexbirthdayNum);
			
		}
	}
	
	
	
	/**
	 * 
	 * 功能 :查询-中心库
	
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
	
}
