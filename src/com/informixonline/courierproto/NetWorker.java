package com.informixonline.courierproto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;

public class NetWorker {
	
	final static String TAG_POST = "NETWORKER";
	//final static String LOGIN_URL = "http://178.64.224.130/fp/cr/data/login.php"; //"http://10.96.95.121/fp/cr/data/login.php";
	//final static String GETDATA_URL ="http://178.64.224.130/fp/cr/data/data.php"; //"http://10.96.95.121/fp/cr/data/data.php";
	//final static String LOGIN_URL = "http://10.96.95.121/fp/cr/data/login.php";
	//final static String GETDATA_URL = "http://10.96.95.121/fp/cr/data/data.php";
	//final static String USER = "1244";
	//final static String PWD = "7765";
	final static String DBGET = "getCourAll";
	final static String DBSND = "SetPOD";
	final static String DBLOGPOD = "courLog";
	
	final int TIMEOUT_CONNECTION = 3000;
	final int TIMEOUT_SOCKET = 5000;
	
    static JSONObject jObj = null;
    static String json = "";
    
    String username = null;
    
    
    public int getData(OrderDbAdapter dbhelper, String dlgloginUser, String dlgloginpwd, String loginURL, String getdataURL) { 
        
/*        // Выключаем проверку работы с сетью в текущем UI потоке (перенесено в CourierMain)
        StrictMode.ThreadPolicy policy = new StrictMode.
        		ThreadPolicy.Builder().permitAll().build();
        		StrictMode.setThreadPolicy(policy);*/
    	int cntNewOrders = 0; // кол-во новых записей
    	
    	// Параметры таймаута подключений
    	HttpParams httpParameters = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONNECTION);
    	HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);
    	
        BufferedReader intro=null;
        DefaultHttpClient cliente=new DefaultHttpClient();
        cliente.setParams(httpParameters);
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
                String loginRes = "false";
                while ((line = intro.readLine()) != null ) {
                	//System.out.println(line);
                	Log.d(TAG_POST, line);
                	
                    // Получение имени пользователя
                	//if (this.username == null) {
	                    try {
	                    	JSONObject jObjlogin = new JSONObject(line);
	    					//Log.d("NETWORKUSER","USERNAME = "+ (String)jObjlogin.get("username"));
	                    	this.username = jObjlogin.getString("username");
	    					Log.d("NETWORKUSER","USERNAME = " + username);
	    					loginRes = jObjlogin.getString("success");
	    						
	    				} catch (Exception e) {
	    					Log.e("JSON Parser", "Error parsing data " + e.toString());
	    					Log.e("JSON Parser", "Source JSON data: " + line);
	    					this.username = "";
	    				}
                	//}
                }
                	   
                if (! loginRes.equals("true")) {
                	return -2; // Неверное имя пользователя или пароль
                }
                intro.close();
                

            } else {
            	// Реагируем на ошибки доступа к серверу
            	return -1;
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
							// Необходимо для определения есть ли такой заказ aNo локально или это новая запись которую надо сохранить локально
							Log.d("NETWORKER", "dbhelper.createOrder " + ord.getString("ano"));
							cntNewOrders = cntNewOrders + 1;
							
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
					// Если не удалось соединится с сервером или на сервере нет данных то локальные записи не удаляются
				} catch (Exception e) {
					Log.e("JSON Parser", "Error parsing data " + e.toString());
					// Здесь можно выставить индикатор нет данных 
					
				}
                

            } else {
            	// Реагируем на ошибки доступа к серверу
            	return -1;
            }
            
        }
        catch (SocketTimeoutException ste) {
        	Log.d(TAG_POST, "SocketTimeoutException " + ste.getMessage());
        	cntNewOrders = -1;
        }
        catch (UnsupportedEncodingException ex)
        {
        	Log.d(TAG_POST, "UnsupportedEncodingException " + ex.getMessage());
        }
        catch(IOException e)
        {
        	// Когда нет связи с сервером
        	Log.d(TAG_POST, "IOException " + e.getMessage());
        	cntNewOrders = -1;
        }
        return cntNewOrders;
    } // End getData
    
    // Передача данных POD
    public int sendData(OrderDbAdapter dbhelper, String dlgloginUser, String dlgloginpwd, String loginURL, String senddataURL, String [] snddata) {
    	int sendResult = 0;
    	// Исключительно для накладных. Данные пересылаются сначала для  dbAct = SetPOD затем dbAct - courLog (event=pod)
        
/*        // Выключаем проверку работы с сетью в текущем UI потоке (перенесено в CourierMain)
        StrictMode.ThreadPolicy policy = new StrictMode.
        		ThreadPolicy.Builder().permitAll().build();
        		StrictMode.setThreadPolicy(policy);*/
    	
    	// Параметры таймаута подключений
    	HttpParams httpParameters = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONNECTION);
    	HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);
    	
        BufferedReader intro=null;
        DefaultHttpClient cliente=new DefaultHttpClient();
        cliente.setParams(httpParameters);
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
        catch (SocketTimeoutException ste) {
        	Log.d(TAG_POST, "SocketTimeoutException " + ste.getMessage());
        	sendResult = -1;
        }
        catch (UnsupportedEncodingException ex)
        {
        	Log.d(TAG_POST, ex.getMessage());
        	sendResult = -2;
        }
        catch(IOException e)
        {
        	// Когда нет связи с сервером
        	Log.d(TAG_POST, e.getMessage());
        	sendResult = -1;
        }
        
        return sendResult;
    } // End sendData    POD
    
    // Передача данных go (inway), ready(isready), view(isview)
    // Возвращает -1 если была ошибка передачи
    public int sendDataGRV(OrderDbAdapter dbhelper, String dlgloginUser, String dlgloginpwd, String loginURL, String senddataURL, String[] snddata) {
    	int sendResult = 0;
    	// String[] snddata = { orderDetail_aNO, event, tdd, "" }
    	// Исключительно для event = go (inway), ready(isready), view(isview) dbAct - courLog 
        
/*        // Выключаем проверку работы с сетью в текущем UI потоке (перенесено в CourierMain)
        StrictMode.ThreadPolicy policy = new StrictMode.
        		ThreadPolicy.Builder().permitAll().build();
        		StrictMode.setThreadPolicy(policy);*/
    	
    	// Параметры таймаута подключений
    	HttpParams httpParameters = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONNECTION);
    	HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);
    	
        BufferedReader intro=null;
        DefaultHttpClient cliente=new DefaultHttpClient();
        cliente.setParams(httpParameters);
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
        
        Log.d("NETWORKER", "SEND data:" + snddata[0] + " " + snddata[1] + " " + snddata[2] + " " + snddata[3]);
        
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
        catch (SocketTimeoutException ste) {
        	sendResult = -1;
        	Log.d(TAG_POST, "SocketTimeoutException " + ste.getMessage());
        }
        catch (UnsupportedEncodingException ex)
        {
        	sendResult = -2;
        	Log.d(TAG_POST, ex.getMessage());
        }
        catch(IOException e)
        {
        	sendResult = -1;
        	Log.d(TAG_POST, e.getMessage());
        }
        catch (Exception e)
        {
        	sendResult = -1;
        	Log.d(TAG_POST, e.getMessage());
        }
        
        return sendResult;
    } // End sendDataGRV

}
