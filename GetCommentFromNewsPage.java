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

public class GetCommentFromNewsPage {
	
	/**
	 * ��ȡ���ŵ����۵�ַ
	 *MalformedURLException
	 */
	public static String getCommentUrl(String url){
		String commentUrl = "";
		if (url.contains("www.wdzj.com/news")){
			String a = url.substring(url.lastIndexOf("/")+1, url.indexOf(".html"));
			
			commentUrl = "http://phpservice.wdzj.com/comment/getHotComment?appid=1&appkey=239111S1ddT02sddsG21qqwwwq%3FsdfkBlsdjlj&type=1&id="+a+"&page=1&limittype=pc";
			//news入口
			try {
				URL u1 = new URL(commentUrl);
				
				HttpURLConnection c1 = (HttpURLConnection) u1.openConnection();
				
				
		        c1.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36 SE 2.X MetaSr 1.0");
		        c1.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		       
		        c1.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
		        c1.addRequestProperty("Referer", commentUrl);
		        c1.addRequestProperty("Connection", "keep-alive");
		        c1.addRequestProperty("X-Requested-With", "XMLHttpRequest");
		       
		        
				
				c1.connect();
				InputStream i1 = c1.getInputStream();
				Scanner s1 = new Scanner(i1, "utf-8");
				StringBuffer z = new StringBuffer();
				while(s1.hasNext()){
					String scsc = s1.nextLine();
					z.append(scsc);
				}
				//�û�ID	
				String regexUserId = "space-([\\d\\D]*?).html"; 
				//�û���
				String regexUserName = "showComment(.*)"; 											
				//�û�����
				String regexUserComment = "(?<=\"content\":\").*?(?=\",\"subjectUrl\")";   //(?<=A).*?(?=B)                           //"class=\"comment-inner mb20\">(.*)</div>"
				//�û����۵�ID
				String regexCommentParentId = "space-([\\d\\D]*?).html";
				
				//    "content":"\u96f7\u4e5f\u5947\u8469\uff0c\u5151\u4ed8\u4e5f\u5947\u8469\u3002","subjectUrl":"
					
				String content = "";  
		        Pattern pattern1 = Pattern.compile(regexUserComment);
		        
		        
		        Matcher matcher = pattern1.matcher(z);  
		        while(matcher.find()) {  
		            content = matcher.group().toString();  
		            System.out.println(content.trim());
		            System.out.println("++++++++++++");
		            String o = content.trim();
		            System.out.println(o);
		        }  
		        
		        System.out.println("-------------------");	
				
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
			
			
			
			
			
		} else {
			commentUrl = url;
			//BBS入口
			
			try {
				URL u1 = new URL(commentUrl);
				
				HttpURLConnection c1 = (HttpURLConnection) u1.openConnection();
				
				
		        c1.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36 SE 2.X MetaSr 1.0");
		        c1.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		       
		        c1.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
		        c1.addRequestProperty("Referer", commentUrl);
		        c1.addRequestProperty("Connection", "keep-alive");
		        c1.addRequestProperty("X-Requested-With", "XMLHttpRequest");
		       
		        
				
				c1.connect();
				InputStream i1 = c1.getInputStream();
				Scanner s1 = new Scanner(i1, "utf-8");
				StringBuffer z = new StringBuffer();
				while(s1.hasNext()){
					String scsc = s1.nextLine();
					z.append(scsc);
				}
				//�û�ID	
				String regexUserId = "space-([\\d\\D]*?).html"; 
				//�û���
				String regexUserName = "showComment(.*)"; 											
				//�û�����
				String regexUserComment = "(?<=class=\"comment-inner mb20\">).*?(?=</div>)";   //(?<=A).*?(?=B)                           //"class=\"comment-inner mb20\">(.*)</div>"
				//�û����۵�ID
				String regexCommentParentId = "space-([\\d\\D]*?).html"; 
				
					
				String content = "";  
		        Pattern pattern1 = Pattern.compile(regexUserComment);
		        
		        
		        Matcher matcher = pattern1.matcher(z);  
		        while(matcher.find()) {  
		            content = matcher.group().toString();  
		            System.out.println(content.trim());
		            
		        }  
		        
		        System.out.println("-------------------");	
				
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
			
		}
		
		return commentUrl;
	}
	
	
	
	
	
	public static void main(String[] args) {
		
		//       http://bbs.wdzj.com/thread-971108-1-1.html
		//       http://www.wdzj.com/news/hydongtai/116723.html
		
		
		String url = "http://www.wdzj.com/news/hydongtai/116723.html";
		
		getCommentUrl(url);
		
	}
	
	
	
	
}
