package com.easemob.chatuidemo.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wenpy.jcc.R;
import android.graphics.Paint.Join;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

public class ConnServer {
    private static final int STATE_ERROR = -1; 
    private static final int STATE_FAIL = 0; 
    private static final int STATE_FINISH = 1; 
    private static final int STATE_LINKERR = 2; 
    private static final int STATE_TIMEOUT = 3; 
    private static final int STATE_NULL = 4; 
	/**
	 * 通过HttpClient发送POST请求
	 * @param path
	 * @param params
	 * @param string
	 * @return
	 */
	private static boolean sendHttpClientPOSTRequire(String path, Map<String, String> params, String encoding) throws Exception {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		if(params != null & !params.isEmpty()){
			for(Map.Entry<String, String> entry : params.entrySet()){
				pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));		
			}
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, encoding);
		HttpPost httpPost = new HttpPost(path);
		httpPost.setEntity(entity);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(httpPost);
		if(response.getStatusLine().getStatusCode() == 200){
			
			return true;
		}
		return false;
	}
	
	public List<Map<String, Object>> sendHttpClientPOSTRequire(String path, Map<String, String> params, String encoding, String tag, String[] item) throws Exception {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		if(params != null & !params.isEmpty()){
			for(Map.Entry<String, String> entry : params.entrySet()){
				pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));		
			}
		}
		System.out.println("url:"+path);
		System.out.println("Post:"+pairs.toString());
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, encoding);
		HttpPost httpPost = new HttpPost(path);
		httpPost.setEntity(entity);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(httpPost);
		if(response.getStatusLine().getStatusCode() == 200){
//			System.out.println("str:"+EntityUtils.toString(response.getEntity()));
			InputStream inStream = new ByteArrayInputStream(EntityUtils.toString(response.getEntity()).getBytes()); 
			List<Map<String, Object>> sdate  = parseXML(inStream,tag,item);
			return sdate;
		}
		return null;
	}

	/**
	 * 发送POST请求
	 * @param path
	 * @param params
	 * @param string
	 * @return
	 */
	public List<Map<String, Object>> sendPOSTRequire(String path,	Map<String, String> params, String encoding,String[] item) throws Exception {
		StringBuilder date = new StringBuilder();
		Log.e("params","params="+params);
		if(params != null & !params.isEmpty()){
			for(Map.Entry<String, String> entry : params.entrySet()){
				date.append(entry.getKey()).append("=");
				String value = entry.getValue();
				if(value == null)
				    value = "";
				date.append(URLEncoder.encode(value, encoding));
				date.append("&");			
			}
			date.deleteCharAt(date.length() - 1);
		}
		Log.d("server",path);
		System.out.println(path+"\r\n"+date);
		byte[] entity = date.toString().getBytes(); //得到实体数据
		HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
		conn.setConnectTimeout(8000);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length",String.valueOf(entity.length));
		OutputStream outStream = conn.getOutputStream();
		outStream.write(entity);
		if(conn.getResponseCode() == 200){
			InputStream inStream = conn.getInputStream();
			/*SAX解析
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();  //获取一个对象  
            SAXParser saxParser = saxFactory.newSAXParser();//利用获取到的对象创建一个解析器  
            XMLContentHandler handler = new XMLContentHandler();//设置defaultHandler  
            saxParser.parse(inStream, handler);//进行解析
            */  
			//PULL解析
			List<Map<String, Object>> sdate  = parseJSON(inStream,item);
//			for(int i=0;i<sdate.size();i++){
//				System.out.println(sdate.get(i).get("status"));
//			}
			return sdate;
		}else{
			System.out.println(conn.getResponseCode()+"");
		}
		return null;
	}
	
	public List<Map<String, Object>> sendPOSTRequire(String path, Map<String, String> params, String encoding, String tag, String[] item) throws Exception {
		StringBuilder date = new StringBuilder();
		if(params != null & !params.isEmpty()){
			for(Map.Entry<String, String> entry : params.entrySet()){
				date.append(entry.getKey()).append("=");
				date.append(URLEncoder.encode(entry.getValue(), encoding));
				date.append("&");			
			}
			date.deleteCharAt(date.length() - 1);
		}
		System.out.println("url:"+path);
		System.out.println("Post:"+date.toString());
		byte[] entity = date.toString().getBytes(); //得到实体数据
		HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
		conn.setConnectTimeout(10000);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length",String.valueOf(entity.length));
		OutputStream outStream = conn.getOutputStream();
		outStream.write(entity);
		if(conn.getResponseCode() == 200){
			InputStream inStream = conn.getInputStream();
			List<Map<String, Object>> sdate  = parseXML(inStream,tag,item);
//			for(int i=0;i<sdate.size();i++){
//				System.out.println(sdate.get(i).get("status"));
//			}			
			return sdate;
		}else{
			System.out.println(conn.getResponseCode()+"");
		}
		return null;
	}

	/**
	 * 发送GET请求
	 * @param path
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public boolean sendGETRequire(String path, Map<String, String> params, String encoding,List list) throws Exception{
		StringBuilder url = new StringBuilder(path);
		url.append("?");
		for(Map.Entry<String, String> entry : params.entrySet()){
			url.append(entry.getKey()).append("=");
			url.append(URLEncoder.encode(entry.getValue(), encoding));
			url.append("&");			
		}
		url.deleteCharAt(url.length() - 1);
		//System.out.println(url.toString());
		HttpURLConnection  conn = (HttpURLConnection)new URL(url.toString()).openConnection();
		conn.setReadTimeout(3000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 200) {
			InputStream inStream = conn.getInputStream();
			List<Map<String, Object>> date  = parseJSON(inStream,list);	
			for(int i=0;i<date.size();i++){
				System.out.println(date.get(i).get("title"));
			}
			return true;
		}
		return false;
	}
	
	/* 
     * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片 
     * 这里的path是图片的地址 
     */  
	public Uri getImageURI(String path, File cache) throws Exception {  
        String name = MD5.getMD5(path) + path.substring(path.lastIndexOf("."));  
//        System.out.println("image_path:"+path);
        File file = new File(cache, name);  
        // 如果图片存在本地缓存目录，则不去服务器下载   
        if (file.exists()) {  
            return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI  
        } else {  
            // 从网络上获取图片  
            URL url = new URL(path);  
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
            conn.setConnectTimeout(5000);  
            conn.setRequestMethod("GET");  
            conn.setDoInput(true);  
            if (conn.getResponseCode() == 200) { 
  
                InputStream is = conn.getInputStream();  
                FileOutputStream fos = new FileOutputStream(file);  
                byte[] buffer = new byte[1024];  
                int len = 0;  
                while ((len = is.read(buffer)) != -1) {  
                    fos.write(buffer, 0, len);  
                }  
                is.close();  
                fos.close();  
                // 返回一个URI对象  
                return Uri.fromFile(file);  
            } else{
                System.out.println("connect code:"+conn.getResponseCode());
            }
        }  
        return null;  
    }
	

    /** 
    * @param path 
    *            要上传的文件路径 
    * @param url 
    *            服务端接收URL 
    * @throws Exception 
    */ 
    public void uploadFile(String path, String url,final Handler handler) throws Exception {  
        File file = new File(path);
        if (file.exists() && file.length() > 0) {  
            AsyncHttpClient client = new AsyncHttpClient();  
            RequestParams params = new RequestParams();  
            params.put("method", "upload_pic"); 
            params.put("uploadedfile", file);
            // 上传文件  
            client.post(url, params, new AsyncHttpResponseHandler() {  
    
                @Override  
                public void onSuccess(int statusCode, Header[] headers,  
                        byte[] responseBody) {  
                    // 上传成功后要做的工作  
                    //Toast.makeText(TalentsAddActivity.this, "上传成功", Toast.LENGTH_LONG).show();  
                    //progress.setProgress(0);
                    ConnServer server = new ConnServer();
                    List<Map<String,Object>> Data = new ArrayList<Map<String,Object>>();

                    Message msg = handler.obtainMessage(); 
                    msg.obj = "upload_pic";
                    Bundle bundle = new Bundle();
                    try {
                        Data = server.parseJSON(responseBody);
                        //System.out.println("responseBody:"+new String(responseBody)+";data:"+Data);
                        if(Boolean.valueOf(Data.get(0).get("status").toString())){
                                msg.what = STATE_FINISH;
                                bundle.putString("url", Data.get(0).get("msg").toString());
                                msg.setData(bundle);
                        }else{
                            msg.what = STATE_FAIL;
                            msg.obj = Data.get(0).get("msg").toString();
                        }
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        msg.what = STATE_ERROR;
                        handler.sendMessage(msg);
                    }
                }  
          
                @Override  
                public void onFailure(int statusCode, Header[] headers,  
                        byte[] responseBody, Throwable error) {  
                    // 上传失败后要做到工作  
                    //Toast.makeText(TalentsAddActivity.this, "上传失败", Toast.LENGTH_LONG).show();  
                }  
          
                @Override  
                public void onProgress(int bytesWritten, int totalSize) {  
                    // TODO Auto-generated method stub  
                    super.onProgress(bytesWritten, totalSize);  
                    int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);  
                    // 上传进度显示  
                    //progress.setProgress(count);  
                    //Log.e("上传 Progress>>>>>", bytesWritten + " / " + totalSize);  
                }  
          
                @Override  
                public void onRetry(int retryNo) {  
                    // TODO Auto-generated method stub  
                    super.onRetry(retryNo);  
                    // 返回重试次数  
                }          
            }); 
        }
    }
    
	/**
	 * 解析String
	 * @param inStream
	 * @return
	 */
	public String parseString(InputStream inStream) throws Exception {

		byte[] bydata = StreamTool.read(inStream);	
		return new String(bydata);
	}
	
	/**
	 * 解析JSON数据
	 * @param inStream
	 * @return
	 */
	public List<Map<String, Object>> parseJSON(InputStream inStream, List list) throws Exception {
		List<Map<String, Object>> data  = new ArrayList<Map<String, Object>>();
		byte[] bydata = StreamTool.read(inStream);
		String json = new String(bydata);
		System.out.println(json);
		if(json.equals("null"))
			return null;
		JSONArray array = new JSONArray(json);
		
		for(int i = 0;i < array.length();i++){
			Map<String, Object> item = new HashMap<String, Object>();
			JSONObject jsonObject = array.getJSONObject(i);
			for(int j = 0;j < list.size();j++){
				item.put(list.get(j).toString(), jsonObject.getString(list.get(j).toString()));				
			}
			data.add(item);
		}
		return data;
	}
	
	public List<Map<String, Object>> parseJSON(InputStream inStream, String[] list) throws Exception {
		List<Map<String, Object>> data  = new ArrayList<Map<String, Object>>();
		byte[] bydata = StreamTool.read(inStream);
		String json = new String(bydata);
		System.out.println(json);
		Log.d("server", json);
		if(json.equals("null")||json.equals(""))
			return data;
		JSONArray array = new JSONArray(json);
		for(int i = 0;i < array.length();i++){
			Map<String, Object> item = new HashMap<String, Object>();
			JSONObject jsonObject = array.getJSONObject(i);
			if(list.length == 0){
				Iterator<?> it = jsonObject.keys();  
				String key = "";  
				String value = null;  
				while(it.hasNext()){//遍历JSONObject   
					key = (String) it.next().toString();  
					value = jsonObject.getString(key);
					if(value.equals("null"))
					    value="";
					item.put(key, value);
				}
			}else
				for(int j = 0;j < list.length;j++){
					item.put(list[j], jsonObject.getString(list[j]));
					//System.out.println(jsonObject.getString(list[j]));
				}
			data.add(item);
		}
		return data;
	}

    public List<Map<String, Object>> parseJSON(byte[] bydata) throws Exception {
        List<Map<String, Object>> data  = new ArrayList<Map<String, Object>>();
        String json = new String(bydata);
        System.out.println(json);
        if(json.equals("null"))
            return null;
        JSONArray array = new JSONArray(json);
        
        for(int i = 0;i < array.length();i++){
            Map<String, Object> item = new HashMap<String, Object>();
            JSONObject jsonObject = array.getJSONObject(i);
            Iterator<?> it = jsonObject.keys();  
            String key = "";  
            String value = null;
            while(it.hasNext()){//遍历JSONObject   
                key = (String) it.next().toString();  
                value = jsonObject.getString(key);
                if(value.equals("null"))
                    value="";
                item.put(key, value);
            }
            data.add(item);
        }
        return data;
    }

	public List<Map<String, Object>> parseXML(InputStream inStream ,String[] list) throws Exception {
		List<Map<String, Object>> data  = new ArrayList<Map<String, Object>>();
		String str;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inStream,"UTF-8");
		int event = parser.getEventType();
		Map<String, Object> item = new HashMap<String, Object>();
		while(event != XmlPullParser.END_DOCUMENT){
			switch (event) {
			case XmlPullParser.START_TAG:
				for(int i=0;i<list.length;i++){
					String tag = parser.getName();
					if(list[i].equals(tag)){
						parser.next();
						System.out.println(list[i]+":"+parser.getText());
						item.put(list[i], parser.getText()==null?"":parser.getText());
					}else 
						item.put(tag, parser.getText());
				}
				break;
			case XmlPullParser.END_TAG:	
				System.out.println("add");
				data.add(item);
				item = new HashMap<String, Object>();
			default:
				break;
			}
			event = parser.next();
		}
		return data;
	}
	
	public List<Map<String, Object>> parseXML(InputStream inStream ,String title, String[] list) throws Exception {
		List<Map<String, Object>> data  = new ArrayList<Map<String, Object>>();
		String str;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inStream,"UTF-8");
		int event = parser.getEventType();
		Map<String, Object> item = new HashMap<String, Object>();
		boolean Stauts = false;
		while(event != XmlPullParser.END_DOCUMENT){
			String tag = parser.getName();
			switch (event) {
			case XmlPullParser.START_TAG:
				if(list.length == 0){
					parser.next();
					System.out.println(tag+":"+parser.getText());
					if(parser.getText() == null){
						item.put(tag,"");
						data.add(item);
					}
					else
						item.put(tag,parser.getText());
				}else{
					for(int i=0;i<list.length;i++){
						if(list[i].equals(tag)){
							parser.next();
							System.out.println(tag+":"+parser.getText());
							item.put(tag, parser.getText());
						}
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if(title.equals(tag)){
					data.add(item);
					item = new HashMap<String, Object>();
				}
			default:
				break;
			}
			event = parser.next();
		}
		return data;
	}
	
	int i=1;
	public class XMLContentHandler extends DefaultHandler {  	        
	        
		private static final String TAG = "XMLContentHandler";  
  
        @Override  
        public void characters(char[] ch, int start, int length)  
                throws SAXException {  
            Log.i(TAG, "解析内容："+new String(ch,start,length));  
        }  
        @Override  
        public void endDocument() throws SAXException {  
            super.endDocument();  
            Log.i(TAG, "文档解析完毕。");  
        }  
        @Override  
        public void endElement(String uri, String localName, String qName)  
                throws SAXException {  
            Log.i(TAG, localName+"解析完毕");  
        }  
        @Override  
        public void startDocument() throws SAXException {  
            Log.i(TAG, "开始解析... ...");  
        }

        @Override
        public void startElement(String uri, String localName, String qName,
			org.xml.sax.Attributes attributes) throws SAXException {
        	if(localName.equals("high")){  
                Log.i(TAG, "解析元素："+localName);  
                i++;  
                   if(i==2){  
                       System.out.println(attributes.getValue(0));                           
                   }  
            }  
        	
		super.startElement(uri, localName, qName, attributes);
        }
	}  
	
	public List<Map<String, Object>> Json2List(String json) throws Exception{
        Log.d("server","data:" +json);
	    List<Map<String, Object>> data  = new ArrayList<Map<String, Object>>();
        if(json.equals("") || json == null)
            return data;
	    JSONArray array;
        try {
            array = new JSONArray(json);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("data",json);
            data.add(item);
            return data;
        }
        for(int i = 0;i < array.length();i++){
            Map<String, Object> item = new HashMap<String, Object>();
            JSONObject jsonObject = array.getJSONObject(i);
            Iterator<?> it = jsonObject.keys();  
            String key = "";  
            String value = null;  
            while(it.hasNext()){//遍历JSONObject   
                key = (String) it.next().toString();  
                value = jsonObject.getString(key);
                if(value.equals("null"))
                    value="";
                item.put(key, value);
            }
            data.add(item);
        }
        return data;
	}	

    public List<Map<String, Object>> GetData(String url,Map<String, String> params, String edit,Handler handler){
        String[] list = new String[]{}; 
        List<Map<String, Object>> ListData = new ArrayList<Map<String,Object>>();
        Message msg = handler.obtainMessage(); 
        //Bundle b = new Bundle();
        msg.obj = edit;
        try {
            ListData = sendPOSTRequire(url,params,"UTF-8",list);
            if(Boolean.valueOf(ListData.get(0).get("status").toString())){
//                if(ListData.get(0).get("data").toString().equals("null")||ListData.get(0).get("data").toString().equals("")){
//                    msg.what = STATE_NULL; 
//                }else
                { 
                    msg.what = STATE_FINISH;
                    ListData = Json2List(ListData.get(0).get("data").toString());
                }
            }else{
                msg.what = STATE_FAIL;
                msg.obj = ListData.get(0).get("msg");
            }
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            msg.what = STATE_ERROR;
            handler.sendMessage(msg);
            return null;
        }
        return ListData;
    }
}
