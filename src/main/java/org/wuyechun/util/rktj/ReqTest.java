package org.wuyechun.util.rktj;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class ReqTest {
	
	public static void main(String [] args){
		WebRequest request=new WebRequest();
		try {
			//Map headerMap=new HashMap();
			//headerMap.put("cardno", "2014818724");
			//headerMap.put("holdername", "卢茹");
			//headerMap.put("holdercardno", "340204197308011029");
			
			String result=request.get("http://localhost:8899/house?cardno=2014818724&holdername=卢茹&holdercardno=340204197308011029",null);
			System.out.println(result);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
