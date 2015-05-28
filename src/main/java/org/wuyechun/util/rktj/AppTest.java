package org.wuyechun.util.rktj;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;

public class AppTest {
	
	public static void main(String []args){
		
		/***
		String a="产权证详细信息083453号";  
		String regEx="[^0-9]";     
		Pattern p = Pattern.compile(regEx);     
		Matcher m = p.matcher(a);     
		System.out.println( m.replaceAll("").trim());  
		****/
		
		String auth=getBase64("test:pass");
		System.out.println(auth);
		
		WebRequest request=new WebRequest();
		
		Map headerMap=new HashMap();
		//headerMap.put("Authorization", "Basic dGVzdDpwYXNz");
		headerMap.put("Authorization", "Basic "+auth);
		
		try {
			String result=request.get("http://59.203.14.147:8080/zjws/api/auth", headerMap);
			
			JSONObject  jasonObject = JSONObject.fromObject(result);
			Map map = (Map)jasonObject; 
			String token=(String) map.get("token");
			
			//String url="http://59.203.14.147:8080/zjws/api/table/T_JGDM?token=6b7447d4-7054-47c1-925a-dc4c72e482dc&from=2015-03-07_00:00:00&to=2015-04-08_00:00:00&offset=0&length=10";
			
			String messageFormat ="{0}/zjws/api/table/T_JGDM?token={1}&from={2}&to={3}&offset={4}&length={5}";    
			String url=MessageFormat.format(messageFormat,"http://59.203.14.147:8080",token, "2015-03-07_00:00:00","2015-04-08_00:00:00","0","10");
			
			String result2=request.get(url, null);
			
			System.out.println(result2);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public static String getBase64(String srcStr){
		byte[] byteOfEncode =Base64.encodeBase64(srcStr.getBytes());
		String tarStr = new String(byteOfEncode);
		return tarStr;
	}

}
