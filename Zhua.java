package cn.zhua;


import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Zhua {

	public static String string2Unicode(String string) {  
		   
	    StringBuffer unicode = new StringBuffer();  
	   
	    for (int i = 0; i < string.length(); i++) {  
	   
	        // 取出每一个字符  
	        char c = string.charAt(i);  
	   
	        // 转换为unicode  
	        unicode.append("\\u" + Integer.toHexString(c));  
	    }  
	   
	    return unicode.toString();  
	}  
	//unicode转换字符串java方法代码片段：  
	//复制代码 代码如下:  
	  
	/** 
	 * unicode 转字符串 
	 */  
	public static String unicode2String(String unicode) {  
	   
	    StringBuffer string = new StringBuffer();  
	   
	    String[] hex = unicode.split("\\\\u");  
	   
	    for (int i = 1; i < hex.length; i++) {  
	   
	        // 转换出每一个代码点  
	        int data = Integer.parseInt(hex[i], 16);  
	   
	        // 追加成string  
	        string.append((char) data);  
	    }  
	   
	    return string.toString();  
	}  
	//测试java代码片段：  
	//复制代码 代码如下:  
	  
	public static void main(String[] args) {  
	    String test = "雷也奇葩，兑付也奇葩。";  
	   
	    String unicode = string2Unicode(test);  
	       
	    String string = unicode2String(unicode) ;  
	       
	    //System.out.println(unicode);  
	       
	    System.out.println(string);  
	   
	}  
}
