package com.informixonline.courierproto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;

public class NetWorker {
	
	final static String TAG_POST = "POST";
	//final static String LOGIN_URL = "http://178.64.224.130/fp/cr/data/login.php"; //"http://10.96.95.121/fp/cr/data/login.php";
	//final static String GETDATA_URL ="http://178.64.224.130/fp/cr/data/data.php"; //"http://10.96.95.121/fp/cr/data/data.php";
	//final static String LOGIN_URL = "http://10.96.95.121/fp/cr/data/login.php";
	//final static String GETDATA_URL = "http://10.96.95.121/fp/cr/data/data.php";
	//final static String USER = "1244";
	//final static String PWD = "7765";
	final static String DBGET = "getCourAll";
	final static String DBSND = "SetPOD";
	final static String DBLOGPOD = "courLog";
	
    static JSONObject jObj = null;
    static String json = "";
    
    String username = null;
    
    
    public void getData(OrderDbAdapter dbhelper, String dlgloginUser, String dlgloginpwd, String loginURL, String getdataURL) { 
        
/*        // Выключаем проверку работы с сетью в текущем UI потоке (перенесено в CourierMain)
        StrictMode.ThreadPolicy policy = new StrictMode.
        		ThreadPolicy.Builder().permitAll().build();
        		StrictMode.setThreadPolicy(policy);*/
    	
        BufferedReader intro=null;
        DefaultHttpClient cliente=new DefaultHttpClient();
        HttpPost post=new HttpPost(loginURL);
        
        List<NameValuePair> nvps = new ArrayList <NameValuePair>();
        List<NameValuePair> nvps_getdata = new ArrayList <NameValuePair>();
        //List<NameValuePair> nvps_snddata = new ArrayList <NameValuePair>();
        
        nvps.add(new BasicNameValuePair("user",dlgloginUser));
        nvps.add(new BasicNameValuePair("password",dlgloginpwd));
        nvps_getdata.add(new BasicNameValuePair("dbAct", DBGET));
        //nvps_snddata.add(new BasicNameValuePair("dbAct", DBSND));
        
        // Регистрация на сервере
        try
        {
            post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8)); //HTTP.UTF_8
            HttpResponse response = cliente.execute(post);
            if(response.getStatusLine().getStatusCode()==200)//this means that you got the page
            {
            	Log.d(TAG_POST, "--- LOGIN return ---");
                HttpEntity entity=response.getEntity();
                intro=new BufferedReader(new InputStreamReader(entity.getContent()));
                String line; //= "";
                while ((line = intro.readLine()) != null ) {
                	//System.out.println(line);
                	Log.d(TAG_POST, line);
                	
                    // Получение имени пользователя
                	if (this.username == null) {
	                    try {
	                    	JSONObject jObjlogin = new JSONObject(line);
	    					//Log.d("NETWORKUSER","USERNAME = "+ (String)jObjlogin.get("username"));
	                    	this.username = jObjlogin.getString("username");
	    					Log.d("NETWORKUSER","USERNAME = " + username);
	    						
	    				} catch (Exception e) {
	    					Log.e("JSON Parser", "Error parsing data " + e.toString());
	    					Log.e("JSON Parser", "Source JSON data: " + line);
	    					this.username = "";
	    				}
                	}
                }
                	                
                intro.close();
                

            }
            
            // Получение отсутствующих данных и удаление несуществующих на сервере
            String aNoListOnServer = ""; // Список заказов принятых с сервера (нужен для удаления локальных записей которые удалены на сервере)
            //DefaultHttpClient cliente=new DefaultHttpClient();
            HttpPost post_data=new HttpPost(getdataURL);
            
            post_data.setEntity(new UrlEncodedFormEntity(nvps_getdata, HTTP.UTF_8)); //HTTP.UTF_8
            Log.d(TAG_POST, "--- BEFORE POST GET DATA ---");
            HttpResponse response_data = cliente.execute(post_data);
            Log.d(TAG_POST, "--- AFTER POST GET DATA ---");
            if(response_data.getStatusLine().getStatusCode()==200)//this means that you got the page
            {
            	Log.d(TAG_POST, "--- got the page DATA ---");
                HttpEntity entity=response_data.getEntity();
                intro=new BufferedReader(new InputStreamReader(entity.getContent()));
                String line = "";
                StringBuilder sbResult =  new StringBuilder();
                while ((line = intro.readLine()) != null ) {
                	//System.out.println(line);
                	sbResult.append(line);
                	Log.d(TAG_POST, line);
                }
                Log.d(TAG_POST, "--- Out buffer: ---" + sbResult.toString());
                	                
                intro.close();
                
                try {
					jObj = new JSONObject(sbResult.toString());
					JSONArray orders = jObj.getJSONArray("data");
					
					for (int i = 0; i < orders.length(); i++) {
						JSONObject ord = orders.getJSONObject(i);
						Log.d(TAG_POST,"number " + i + " client " + ord.getString("client")
								+ " rcpn " + ord.getString("rcpn"));
						
						aNoListOnServer = "'" + ord.getString("ano") + "' , " + aNoListOnServer;
						if (dbhelper.isNewOrder(ord.getString("ano"))) {
							Log.d("NETWORKER", "dbhelper.createOrder " + ord.getString("ano"));
							
							dbhelper.createOrder(ord.getString("ano"),
											ord.getString("displayno"),
											ord.getString("acash"),
											ord.getString("aaddress"),
											ord.getString("client"),
											ord.getString("timeb"),
											ord.getString("timee"),
											ord.getString("tdd"),
											ord.getString("cont"),
											ord.getString("contphone"),
											ord.getString("packs"),
											ord.getString("wt"),
											ord.getString("volwt"),
											ord.getString("rems"),
											ord.getString("ordstatus"),
											ord.getString("ordtype"),
											ord.getString("rectype"),
											ord.getString("isredy"),									
											ord.getString("inway"),
											ord.getString("isview"),
											ord.getString("rcpn")
								);
						}
						
					}
					
					aNoListOnServer = aNoListOnServer + " 'test'";
					Log.d("NETWORKER", aNoListOnServer);
					// Удаляем несуществующие на сервере записи
					dbhelper.deleteNotExistOrd(aNoListOnServer);
					
				} catch (Exception e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
				}
                

            } 
            
        }
        catch (UnsupportedEncodingException ex)
        {
        	Log.d(TAG_POST, ex.getMessage());
        }
        catch(IOException e)
        {
        	Log.d(TAG_POST, e.getMessage());
        }
    }
    
    // Передача данных POD
    public void sendData(OrderDbAdapter dbhelper, String dlgloginUser, String dlgloginpwd, String loginURL, String senddataURL, String [] snddata) { 
    	// Исключительно для накладнах. Данные пересылаются сначала для  dbAct = SetPOD затем dbAct - courLog (event=pod)
        
/*        // Выключаем проверку работы с сетью в текущем UI потоке (перенесено в CourierMain)
        StrictMode.ThreadPolicy policy = new StrictMode.
        		ThreadPolicy.Builder().permitAll().build();
        		StrictMode.setThreadPolicy(policy);*/
    	
        BufferedReader intro=null;
        DefaultHttpClient cliente=new DefaultHttpClient();
        HttpPost post=new HttpPost(loginURL);
        
        List<NameValuePair> nvps = new ArrayList <NameValuePair>();
        List<NameValuePair> nvps_snddata = new ArrayList <NameValuePair>();
        List<NameValuePair> nvps_logpoddata = new ArrayList <NameValuePair>();
        
        nvps.add(new BasicNameValuePair("user",dlgloginUser));
        nvps.add(new BasicNameValuePair("password",dlgloginpwd));
        // Данные POD
        nvps_snddata.add(new BasicNameValuePair("dbAct", DBSND));
        nvps_snddata.add(new BasicNameValuePair("wb_no", snddata[0]));
        nvps_snddata.add(new BasicNameValuePair("p_d_in", snddata[1]));
        nvps_snddata.add(new BasicNameValuePair("tdd", snddata[2]));
        nvps_snddata.add(new BasicNameValuePair("rcpn", snddata[3]));
        // Данные действий для event=POD
        nvps_logpoddata.add(new BasicNameValuePair("dbAct", DBLOGPOD));
        nvps_logpoddata.add(new BasicNameValuePair("ano", snddata[0]));
        nvps_logpoddata.add(new BasicNameValuePair("event", "pod"));
        nvps_logpoddata.add(new BasicNameValuePair("eventtime", snddata[1] + " " + snddata[2]));
        nvps_logpoddata.add(new BasicNameValuePair("rem", ""));
        
        
        
        // Регистрация на сервере
        try
        {
            post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8)); //HTTP.UTF_8
            HttpResponse response = cliente.execute(post);
            if(response.getStatusLine().getStatusCode()==200)//this means that you got the page
            {
            	Log.d(TAG_POST, "--- LOGIN return ---");
                HttpEntity entity=response.getEntity();
                intro=new BufferedReader(new InputStreamReader(entity.getContent()));
                String line = "";
                while ((line = intro.readLine()) != null ) {
                	//System.out.println(line);
                	Log.d(TAG_POST, line);
                }
                	                
                intro.close();
            }
            
            // Отправка данных
            HttpPost post_data=new HttpPost(senddataURL);
            
            post_data.setEntity(new UrlEncodedFormEntity(nvps_snddata, HTTP.UTF_8)); //HTTP.UTF_8
            Log.d(TAG_POST, "--- BEFORE POST SEND DATA ---");
            HttpResponse response_data = cliente.execute(post_data);
            Log.d(TAG_POST, "--- AFTER POST SEND DATA ---");
            if(response_data.getStatusLine().getStatusCode()==200)//this means that you got the page
            {
            	Log.d(TAG_POST, "--- got the page DATA ---");
                HttpEntity entity=response_data.getEntity();
                intro=new BufferedReader(new InputStreamReader(entity.getContent()));
                String line = "";
                StringBuilder sbResult =  new StringBuilder();
                while ((line = intro.readLine()) != null ) {
                	//System.out.println(line);
                	sbResult.append(line);
                	Log.d(TAG_POST, line);
                }
                Log.d(TAG_POST, "--- Out SEND buffer: ---" + sbResult.toString());
                	                
                intro.close();
                
            } 
            
            // пересылка данных courLog для POD
            post_data.setEntity(new UrlEncodedFormEntity(nvps_logpoddata, HTTP.UTF_8)); //HTTP.UTF_8
            Log.d(TAG_POST, "--- BEFORE POST SEND DATA ---");
            response_data = cliente.execute(post_data);
            Log.d(TAG_POST, "--- AFTER POST SEND DATA ---");
            if(response_data.getStatusLine().getStatusCode()==200)//this means that you got the page
            {
            	Log.d(TAG_POST, "--- got the page DATA ---");
                HttpEntity entity=response_data.getEntity();
                intro=new BufferedReader(new InputStreamReader(entity.getContent()));
                String line = "";
                StringBuilder sbResult =  new StringBuilder();
                while ((line = intro.readLine()) != null ) {
                	//System.out.println(line);
                	sbResult.append(line);
                	Log.d(TAG_POST, line);
                }
                Log.d(TAG_POST, "--- Out SEND buffer: ---" + sbResult.toString());
                	                
                intro.close();
                
            }  
            
        }
        catch (UnsupportedEncodingException ex)
        {
        	Log.d(TAG_POST, ex.getMessage());
        }
        catch(IOException e)
        {
        	Log.d(TAG_POST, e.getMessage());
        }
    }
    
    // Передача данных go (inway), ready(isready), view(isview)
    public void sendDataGRV(OrderDbAdapter dbhelper, String dlgloginUser, String dlgloginpwd, String loginURL, String senddataURL, String[] snddata) { 
    	// String[] snddata = { orderDetail_aNO, event, tdd, "" }
    	// Исключительно для event = go (inway), ready(isready), view(isview) dbAct - courLog 
        
/*        // Выключаем проверку работы с сетью в текущем UI потоке (перенесено в CourierMain)
        StrictMode.ThreadPolicy policy = new StrictMode.
        		ThreadPolicy.Builder().permitAll().build();
        		StrictMode.setThreadPolicy(policy);*/
    	
        BufferedReader intro=null;
        DefaultHttpClient cliente=new DefaultHttpClient();
        HttpPost post=new HttpPost(loginURL);
        
        List<NameValuePair> nvps = new ArrayList <NameValuePair>();
        //List<NameValuePair> nvps_snddata = new ArrayList <NameValuePair>();
        List<NameValuePair> nvps_logpoddata = new ArrayList <NameValuePair>();
        
        nvps.add(new BasicNameValuePair("user",dlgloginUser));
        nvps.add(new BasicNameValuePair("password",dlgloginpwd));
        // Данные действий для event=courLog
        nvps_logpoddata.add(new BasicNameValuePair("dbAct", DBLOGPOD));
        nvps_logpoddata.add(new BasicNameValuePair("ano", snddata[0]));
        nvps_logpoddata.add(new BasicNameValuePair("event", snddata[1]));
        nvps_logpoddata.add(new BasicNameValuePair("eventtime", snddata[2]));
        nvps_logpoddata.add(new BasicNameValuePair("rem", snddata[3]));
        
        
        
        // Регистрация на сервере
        try
        {
            post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8)); //HTTP.UTF_8
            HttpResponse response = cliente.execute(post);
            if(response.getStatusLine().getStatusCode()==200)//this means that you got the page
            {
            	Log.d(TAG_POST, "--- LOGIN return ---");
                HttpEntity entity=response.getEntity();
                intro=new BufferedReader(new InputStreamReader(entity.getContent()));
                String line = "";
                while ((line = intro.readLine()) != null ) {
                	//System.out.println(line);
                	Log.d(TAG_POST, line);
                }
                	                
                intro.close();
            }
            
            // Отправка данных
            HttpPost post_data=new HttpPost(senddataURL);
            
            //post_data.setEntity(new UrlEncodedFormEntity(nvps_snddata, HTTP.UTF_8)); //HTTP.UTF_8
            //Log.d(TAG_POST, "--- BEFORE POST SEND DATA ---");
            HttpResponse response_data;// = cliente.execute(post_data);
            //Log.d(TAG_POST, "--- AFTER POST SEND DATA ---");

            
            // пересылка данных courLog для POD
            post_data.setEntity(new UrlEncodedFormEntity(nvps_logpoddata, HTTP.UTF_8)); //HTTP.UTF_8
            Log.d(TAG_POST, "--- BEFORE POST SEND DATA ---");
            response_data = cliente.execute(post_data);
            Log.d(TAG_POST, "--- AFTER POST SEND DATA ---");
            if(response_data.getStatusLine().getStatusCode()==200)//this means that you got the page
            {
            	Log.d(TAG_POST, "--- got the page DATA ---");
                HttpEntity entity=response_data.getEntity();
                intro=new BufferedReader(new InputStreamReader(entity.getContent()));
                String line = "";
                StringBuilder sbResult =  new StringBuilder();
                while ((line = intro.readLine()) != null ) {
                	//System.out.println(line);
                	sbResult.append(line);
                	Log.d(TAG_POST, line);
                }
                Log.d(TAG_POST, "--- Out SEND buffer: ---" + sbResult.toString());
                	                
                intro.close();
                
            }  
            
        }
        catch (UnsupportedEncodingException ex)
        {
        	Log.d(TAG_POST, ex.getMessage());
        }
        catch(IOException e)
        {
        	Log.d(TAG_POST, e.getMessage());
        }
    }

}
