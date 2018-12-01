package zhua;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

public class CreatehttpClient {

	// ��һ�ַ���
		//���ַ�������apache�ṩ�İ�,�򵥷���
		//����Ҫ�õ����°�:commons-codec-1.4.jar
		// commons-httpclient-3.1.jar
		// commons-logging-1.0.4.jar
		public static String createhttpClient(String url, String param) {
		  HttpClient client = new HttpClient();
		  String response = null;
		  String keyword = null;
		  PostMethod postMethod = new PostMethod(url);
		//  try {
		//   if (param != null)
//		    keyword = new String(param.getBytes("gb2312"), "ISO-8859-1");
		//  } catch (UnsupportedEncodingException e1) {
		//   // TODO Auto-generated catch block
		//   e1.printStackTrace();
		//  }
		  // NameValuePair[] data = { new NameValuePair("keyword", keyword) };
		  // // ������ֵ����postMethod��
		  // postMethod.setRequestBody(data);
		  // ���ϲ����Ǵ�����ץȡ,���Լ�����ע���ˣ���ҿ��԰�ע�������о���
		  try {
		   int statusCode = client.executeMethod(postMethod);
		   response = new String(postMethod.getResponseBodyAsString()
		     .getBytes("ISO-8859-1"), "gb2312");
		     //����Ҫע���� gb2312Ҫ����ץȡ��ҳ�ı���Ҫһ��
		   String p = response.replaceAll("//&[a-zA-Z]{1,10};", "")
		     .replaceAll("<[^>]*>", "");//ȥ����ҳ�д���html���Եı�ǩ
		   System.out.println(p);
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		  return response;
		}
		// �ڶ��ַ���
		// ���ַ�����JAVA�Դ���URL��ץȡ��վ����
		public String getPageContent(String strUrl, String strPostRequest,
		   int maxLength) {
		  // ��ȡ�����ҳ
		  StringBuffer buffer = new StringBuffer();
		  System.setProperty("sun.net.client.defaultConnectTimeout", "5000");
		  System.setProperty("sun.net.client.defaultReadTimeout", "5000");
		  try {
		   URL newUrl = new URL(strUrl);
		   HttpURLConnection hConnect = (HttpURLConnection) newUrl
		     .openConnection();
		   // POST��ʽ�Ķ�������
		   if (strPostRequest.length() > 0) {
		    hConnect.setDoOutput(true);
		    OutputStreamWriter out = new OutputStreamWriter(hConnect
		      .getOutputStream());
		    out.write(strPostRequest);
		    out.flush();
		    out.close();
		   }
		   // ��ȡ����
		   BufferedReader rd = new BufferedReader(new InputStreamReader(
		     hConnect.getInputStream()));
		   int ch;
		   for (int length = 0; (ch = rd.read()) > -1
		     && (maxLength <= 0 || length < maxLength); length++)
		    buffer.append((char) ch);
		   String s = buffer.toString();
		   s.replaceAll("//&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "");
		   System.out.println(s);
		   rd.close();
		   hConnect.disconnect();
		   return buffer.toString().trim();
		  } catch (Exception e) {
		   // return "����:��ȡ��ҳʧ�ܣ�";
		   //
		   return null;
		  }
		}
		
}
