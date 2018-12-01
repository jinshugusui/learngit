package com.longriver.netpro.webview.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.longriver.netpro.common.MQSender.MQSenderCopors;
import com.longriver.netpro.util.MQDataSave;
import com.longriver.netpro.util.UrlUtil;
import com.longriver.netpro.webview.entity.CoporsTaskBean;

public class WY163NewsFetch {
	public static void toRun(CoporsTaskBean coporsTaskBean) throws Exception {
		String newId = coporsTaskBean.getUrl();
		if(newId.contains("html")){
			newId = newId.substring(newId.lastIndexOf("/")+1, newId.indexOf(".html"));
		}
		if(newId.contains("htm")){
			newId = newId.substring(newId.lastIndexOf("/")+1, newId.indexOf(".htm"));
		}
//		String regexNewsId = "\"content\":\"([\\d\\D]*?)\",";  
//		String content = getContent(regexContent,commentList.get(j));
		try{
			int pageSize = getPageSize(coporsTaskBean);
			
			for(int i=0;i<=pageSize;i++){
					try{
						String  testTemp = cj( UrlUtil.get163CommentUrl(coporsTaskBean.getUrl()),i);
						testTemp = testTemp.replace(");", "");
						System.out.println(testTemp);
						List<String> commentList = new ArrayList<String>();
						
						
						 net.sf.json.JSONObject jsonObject =net.sf.json.JSONObject.fromObject(testTemp);  
						 net.sf.json.JSONArray commentIds = jsonObject.getJSONArray("commentIds");  
						 net.sf.json.JSONObject comments = jsonObject.getJSONObject("comments"); 
						 net.sf.json.JSONObject row = null;  
						 JSONObject jsonFirst = new JSONObject();
							
							jsonFirst.put("collectionId", coporsTaskBean.getCollectionId());
							jsonFirst.put("taskUrl", coporsTaskBean.getUrl());
							if(coporsTaskBean.getFetchType()==null){
								jsonFirst.put("fetchType", "1");
							}else
								jsonFirst.put("fetchType", coporsTaskBean.getFetchType());
							jsonFirst.put("areaproject", coporsTaskBean.getAreaProject());
								//创建JSONArray数组，并将json添加到数组
						    JSONArray array = new JSONArray();
						 for(int j=0;j<commentIds.size();j++){
							 
							 try{
								 
									
									 if(((String)commentIds.get(j)).contains(",")){
										 String commentIdttt[] = ((String)commentIds.get(j)).split(",");
										 for(int k=0;k<commentIdttt.length;k++){
											 net.sf.json.JSONObject cooment = comments.getJSONObject(commentIdttt[k]);
												
											 
											 net.sf.json.JSONObject userInfo = cooment.getJSONObject("user");
											
											 String content =cooment.get("content").toString();
											 String postTime = (String)cooment.get("createTime").toString();
											 String nickName ="";
											 String fromLocation = "";
											 String userImage="";
											 if(userInfo!=null){
												 if(userInfo.get("nickname")!=null){
													 nickName =userInfo.get("nickname").toString();
												 }
												 if(userInfo.get("avatar")!=null){
													 userImage = userInfo.get("avatar").toString();
												 }
												 if(userInfo.get("location")!=null){
													 fromLocation =  userInfo.get("location").toString();
												 }
												 
												 System.out.println(nickName);
											 }
											 if(fromLocation.equals("")){
												 fromLocation = "http://mimg.126.net/p/butter/1008031648/img/face_big.gif";
											 }
											 String commentId = cooment.get("commentId").toString();
											
											 JSONObject json = new JSONObject();
									    		json.put("nickName", nickName);
												json.put("comment", content);
												json.put("fromLocation", fromLocation);
									  			json.put("userImag", userImage);
												json.put("postTime", postTime);
												json.put("commentId", newId+"_"+commentId);
												json.put("collectionTaskId", coporsTaskBean.getCollectionId());
												
											    array.put(json);
										 }
									 }else{
										 net.sf.json.JSONObject cooment = comments.getJSONObject((String)commentIds.get(j));
											
										 
										 net.sf.json.JSONObject userInfo = cooment.getJSONObject("user");
										 
										 String content =cooment.get("content").toString();
										 String postTime = (String)cooment.get("createTime").toString();
										 String nickName ="";
										 String fromLocation = "";
										 String userImage="";
										 if(userInfo!=null){
											 if(userInfo.get("nickname")!=null){
												 nickName =userInfo.get("nickname").toString();
											 }
											 if(userInfo.get("avatar")!=null){
												 userImage = userInfo.get("avatar").toString();
											 }
											 if(userInfo.get("location")!=null){
												 fromLocation =  userInfo.get("location").toString();
											 }
											 System.out.println(nickName);
										 }
										 String commentId = cooment.get("commentId").toString();
										
										 JSONObject json = new JSONObject();
								    		json.put("nickName", nickName);
											json.put("comment", content);
											json.put("postTime", postTime);
											json.put("fromLocation", fromLocation);
								  			json.put("userImag", userImage);
											json.put("commentId", newId+"_"+commentId);
											json.put("collectionTaskId", coporsTaskBean.getCollectionId());
											
										    array.put(json);
									 }
							 }catch(Exception ej){
								 ej.printStackTrace();
							 } 
						 }
						
						 jsonFirst.put("comments", array);
							String jsonStr = jsonFirst.toString();
							
							//MQSenderCopors.sender.sendJsonFetch(jsonStr);
							//MQSenderCopors.getMessage(coporsTaskBean.getAreaProject()).sendJsonFetch(jsonStr);	
							try {
								MQSenderCopors.getMessage(coporsTaskBean.getAreaProject()).sendJsonFetch(jsonStr);	
							}catch(Exception e){
								MQDataSave.writeTxtFileFetch(jsonStr);
								e.printStackTrace();
							}
						
						
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static int getPageSize(CoporsTaskBean coporsTaskBean) throws Exception {
//		String url = "http://comment.news.163.com/news3_bbs/BBDSN4HM00014AED.html";
		String url = coporsTaskBean.getUrl();
		url = UrlUtil.get163CommentUrl(url);
		String boardId = url.substring(url.lastIndexOf("/")+1,url.indexOf(".html"));		
		
		//String m = "";
		String tcount = "";
		
		URL u2 = new URL("http://comment.news.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads/"+boardId+"?callback=getData");
		HttpURLConnection c2 = (HttpURLConnection) u2.openConnection();
		c2.connect();
		InputStream i2 = c2.getInputStream();
		Scanner s2 = new Scanner(i2, "utf-8");
		StringBuffer a = new StringBuffer();
		while(s2.hasNext()){
			String scsc = s2.nextLine();
			a.append(scsc);
			
			
		}
		String regexCount = "\"tcount\":([\\d\\D]*?),";  
		tcount = getContent(regexCount,a.toString()); 
		int sumCount = 0;
		int pageSize=0;
		if(!tcount.equals("")){
			sumCount = Integer.parseInt(tcount);
			pageSize = (int)sumCount/30;
		}
		return pageSize;
	}
	public static String cj(String url,int pageSize) throws Exception {
		StringBuffer  aa = new StringBuffer();
		String boardId = url.substring(url.lastIndexOf("/")+1,url.indexOf(".html"));		
		int offset = 0;
		int limit = 30;
		//for(int i=pageSize-3;i<pageSize;i++){
			
			offset = limit * pageSize;

			URL uf = new URL("http://comment.news.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads/"+boardId+"/comments/newList?&offset=" + offset + "&limit=" + limit + "&showLevelThreshold=72&headLimit=1&tailLimit=2&callback=getData&_=" + new Date().getTime());

	        HttpURLConnection cf = (HttpURLConnection) uf.openConnection();
			
	        cf.setRequestProperty("contentType", "GBK");  
	        cf.setRequestProperty("Content-type", "text/html");
	        cf.setRequestProperty("Accept-Charset", "utf-8");
	        cf.setRequestProperty("contentType", "utf-8");
	        
			cf.addRequestProperty("Host", "comment.news.163.com");
			cf.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:40.0) Gecko/20100101 Firefox/40.0");
			cf.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			cf.addRequestProperty("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
			cf.addRequestProperty("Connection", "keep-alive");
			cf.addRequestProperty("X-Requested-With", "XMLHttpRequest");
			cf.connect();
			 
			BufferedReader sf = new BufferedReader(new InputStreamReader(cf.getInputStream(),"utf-8"));

			while(sf.readLine()!=null){
				String scsc = sf.readLine();
				aa.append(scsc);
			}
						
		return aa.toString();
		
	}
	
	
	private static String getContent(String regex,String text) {  
        String content = "";  
        Pattern pattern = Pattern.compile(regex);  
        Matcher matcher = pattern.matcher(text);  
        while(matcher.find()) {  
            content = matcher.group(1).toString();  
            System.out.println(content);
        }  
        return content;
    }  
	private static List<String> getContentList(String regex,String text) {  
        String content = "";  
        List<String> commentList = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regex);  
        Matcher matcher = pattern.matcher(text);  
        while(matcher.find()) {  
            content = matcher.group(1).toString();  
            commentList.add(content);
        }  
        return commentList;
    }  
	public static void main(String agrs[]){
		CoporsTaskBean bb = new CoporsTaskBean();
		bb.setUrl("http://news.163.com/17/0607/23/CMC8Q3O4000189FH.html");
		bb.setCollectionId("222");
		try {
			toRun(bb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}
}
