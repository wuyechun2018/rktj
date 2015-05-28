package org.wuyechun.util.rktj;

import java.util.Timer;
import java.util.TimerTask;

public class TimeRequest {

	/**
	 * 
	 * 功能 :延迟1000毫秒后执行，然后每隔1000毫秒执行一次。
	
	 * 开发：ycwu3 2015-5-20
	
	 * @param args
	 */
	public static void main(String []args){
		Timer timer = new Timer();  
		        timer.schedule(new TimerTask() {  
		            public void run() {  
		            	doBiz();
		            }  
		       }, 1000,1000);
		
	}
	
	
	public static void doBiz(){
		
	}
	
}
