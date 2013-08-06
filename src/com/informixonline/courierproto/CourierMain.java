package com.informixonline.courierproto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.informixonline.courierproto.OrderDbAdapter;
import com.informixonline.courierproto.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

//@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
@TargetApi(16)
public class CourierMain extends Activity implements OnClickListener {
	
	static long ordersId; // ID заказа выбранного в списке для подсветки через CustomAdapter
	private byte SQLSORTORDER = 0; // флаг признака сортировки: 0 desc 1 asc для запоминания (после каждого нажатия кнопки меняется) 

	// Ключи сетевых настроек (используется в ActSettings и NetWorker) для доступа к хранению настроек
	final static String SHAREDPREF = "sharedstore";
	final static String APPCFG_LOGIN = "LOGIN"; // предыдущий логин в программе, чтобы определить, удалять старые локальные данные или нет
	final static String APPCFG_LOGIN_URL = "LOGIN_URL";
	final static String APPCFG_GETDATA_URL = "GETDATA_URL";
	final static String APPCFG_ADDR_URL = "ADDRURL";
	
	// Коды активити для получения результата Activity Return Code - ARC
	final int ARC_NUMITEMS = 1; // Активити Кол-во отправлений 
	final int ARC_POD = 2; // Активити ПОД
	
	Cursor cursor;
	ListView listView;
	NetWorker nwork = new NetWorker();
	
	// Список переменных для передачи в детальную форму 
	static String recType_forDetail = "N"; // 0 - заказ, 1 - накладная, 2 - счет
	static String orderDetail_aNO;
	static String tvDorder_state_ordStatus;
	static String tvDorder_type_ordType;
	static String tvDacash;
	static String tvDaddr_aAddress;
	static String tvDcomp_name_client;
	static String tvDcontact_Cont;
	static String tvDcontact_num_ContPhone;
	static String tvDtimeBE;
	static String tvDpos_num_Packs;
	static String tvDweight_Wt;
	static String tvDvol_weight_VolWt;
	static String tvDcomment;
	static String tvLocNumItems;
	static String tvDIsredy;
	static String tvDInway;

	// Идентификаторы контекстного меню списка (при длительном нажатии на элемент списка)
	private static final int CM_CATCH_ORDER = 0;
	private static final int CM_RET_ORDER = 1;
	private static final int CM_BACK_ORDER = 2;
	
	private OrderDbAdapter dbHelper;
	// private SimpleCursorAdapter dataAdapter;
	private MyCursorAdapter dataAdapter;
	
	// Кнопки главной активити
	Button btnAddr, btnClient, btnTime, btnType, btnExit, btnInWay, btnOk, btnPod, btnDetail, btnNumItems, btnAll; //btnSettings
	
	TextView tvCourName, tvRefrTime, tvNewRecs, tvAllRecs; // tvNewAllRecs статусная строка
	ImageView imgvSrvOff, imgvSrvOn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_courier_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		// Получаем сохраненные сетевые настройки
    	SharedPreferences sharedAppConfig;
    	sharedAppConfig = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
    	this.login_URL = sharedAppConfig.getString(APPCFG_LOGIN_URL, "");
    	this.getdata_URL = sharedAppConfig.getString(APPCFG_GETDATA_URL, "");
    	this.prev_user = sharedAppConfig.getString(APPCFG_LOGIN, "nologin");
		Log.d("CourierMain.getNetworkData", "Login URL = " + login_URL + " GetData URL = " + getdata_URL);
		
		// Строка статуса
		tvCourName = (TextView)findViewById(R.id.tvCourName);
		tvRefrTime = (TextView)findViewById(R.id.tvRefrTime);
		//tvNewAllRecs = (TextView)findViewById(R.id.tvNewAllRecs);
		//tvNewAllRecs.setText("");
		
		tvAllRecs = (TextView)findViewById(R.id.tvAllRecs);
		tvAllRecs.setText("");
		tvNewRecs = (TextView)findViewById(R.id.tvNewRecs);
		tvNewRecs.setText("");
		
		imgvSrvOn = (ImageView)findViewById(R.id.imgvSrvOn);
		imgvSrvOff = (ImageView)findViewById(R.id.imgvSrvOff);
		imgvSrvOff.setVisibility(View.INVISIBLE);
		

		// Показываем диалог логина и ждем ввода
		//boolean res = false;
		showLogin();
		
        // Выключаем проверку работы с сетью в текущем UI потоке
        StrictMode.ThreadPolicy policy = new StrictMode.
        		ThreadPolicy.Builder().permitAll().build();
        		StrictMode.setThreadPolicy(policy);
        
        Log.d("CourierMain", "--- After showLogin()");
		dbHelper = new OrderDbAdapter(this);
		dbHelper.open();

		// Clean all data
		//dbHelper.deleteAllOrders(); // удаляем старые данные перед работой
		// Add some data
		//dbHelper.insertTestEntries();
		//Log.d("POST", "--- DELETE ALL orders before connect ---");

		
		// Отправляем данные накопившиеся в оффлайн если они есть только после успешного логина
		// sendOfflineData();
		
		// Кнопки на главной активити
		btnAddr = (Button)findViewById(R.id.btnAddr);
		btnAddr.setOnClickListener(this);
		
		btnClient = (Button)findViewById(R.id.btnClient);
		btnClient.setOnClickListener(this);
		
		btnTime = (Button)findViewById(R.id.btnTime);
		btnTime.setOnClickListener(this);
		
		btnType = (Button)findViewById(R.id.btnType);
		btnType.setOnClickListener(this);
		
		//btnSettings = (Button)findViewById(R.id.btnSaveSet);
		//btnSettings.setOnClickListener(this);
		
		btnExit = (Button)findViewById(R.id.btnExit);
		btnExit.setOnClickListener(this);
		
		btnInWay = (Button)findViewById(R.id.btnInWay);
		btnInWay.setOnClickListener(this);
		btnOk = (Button)findViewById(R.id.btnOk);
		btnOk.setOnClickListener(this);
		btnPod = (Button)findViewById(R.id.btnPod);
		btnPod.setOnClickListener(this);
		btnDetail = (Button)findViewById(R.id.btnDetail);
		btnDetail.setOnClickListener(this);
		btnNumItems = (Button)findViewById(R.id.btnNumItems);
		btnNumItems.setOnClickListener(this);
		btnAll = (Button)findViewById(R.id.btnAll);
		btnAll.setOnClickListener(this);	
		// Generate ListView from SQLite Database
		// displayListView(); moved to dialog

	}
	
	// Получение результатов от опр.активити в главной активити (опр.по коду requestCode)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ARC_NUMITEMS:
			// Активити Кол-во отправлений - обновляем если нажато Ок
			if (resultCode == RESULT_OK) {
				String numItems = data.getStringExtra("numitems");
				long rowid = data.getLongExtra("ordersid", 0);
				Log.d("CourierMain.onActivityResult", "numItems = " + numItems
						+ " rowid = " + rowid);
				dbHelper.updLocNumItems(rowid, numItems);
				// обновляем курсор
				cursor.requery();
				dataAdapter.swapCursor(dbHelper.fetchOrders(0));
				dataAdapter.notifyDataSetChanged();
			} else {
				Log.d("CourierMain.onActivityResult", "Numitems Result cancel");
			}
			break;
			
		case ARC_POD:
			if (resultCode == RESULT_OK) {
				// Активити ПОД (исключительно для накладных)
				Log.d("CourierMain.onActivityResult",
						"Result from POD Activity");
				long rowid = data.getLongExtra("ordersid", 0);
				String wb_no = data.getStringExtra("wb_no");
				String p_d_in = data.getStringExtra("p_d_in");
				String tdd = data.getStringExtra("tdd");
				String rcpn = data.getStringExtra("rcpn");
				String[] snddata = { wb_no, p_d_in, tdd, rcpn };
				Log.d("CourierMain.onActivityResult", "wb_no = " + wb_no
						+ " p_d_in = " + p_d_in + " tdd = " + tdd + " rcpn = "
						+ rcpn + " rowid = " + rowid);
				
				int sendResult = nwork.sendData(this.dbHelper, this.user, this.pwd,
				this.login_URL, this.getdata_URL, snddata);
				if (sendResult == -1) {
					// Нет сети - сохраняем данные snddata в оффлайн хранилище
					dbHelper.saveSnddata("SetPOD", snddata); // 6|SetPOD|1567-3118|20130603|14:54|testoffline1
				}
				
				// обновление времени tdd
				dbHelper.updPodTime(rowid, tdd);
				cursor.requery();
				dataAdapter.swapCursor(dbHelper.fetchOrders(0));
				dataAdapter.notifyDataSetChanged();
			} else {
				Log.d("CourierMain.onActivityResult", "POD Result cancel");
			}
			break;

		default:
			Log.d("CourierMain.onActivityResult", "WARNING: undefined activity requestCode!");
			break;
		}
	}
	
	private int getNetworkData(NetWorker nwork, String user, String pwd) {
		int res = 0;
		// Проверяем сетевые разрешения
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			
			Log.d("CourierMain.getNetworkData", "--- Network OK ---");
			
	    	// Получаем сохраненные сетевые настройки
/*	    	SharedPreferences sharedAppConfig;
	    	sharedAppConfig = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
	    	this.login_URL = sharedAppConfig.getString(APPCFG_LOGIN_URL, "");
	    	this.getdata_URL = sharedAppConfig.getString(APPCFG_GETDATA_URL, "");
			Log.d("CourierMain.getNetworkData", "Login URL = " + login_URL + " GetData URL = " + getdata_URL);
*/	    	
		    res = nwork.getData(dbHelper, user, pwd, login_URL, getdata_URL);
		    
		    // Устанавливаем статусную строку
		    this.tvCourName.setText(nwork.username);
		    this.tvRefrTime.setText(this.getDateTimeEvent(1));
		    int cntrecsall = dbHelper.getCountOrd();
		    int cntnewrecs = dbHelper.getNewCountOrd();
		    //this.tvNewAllRecs.setText("0/" + Integer.toString(cntrecsall)); 
		    this.tvAllRecs.setText(Integer.toString(cntrecsall));
		    this.tvNewRecs.setText(Integer.toString(cntnewrecs));
			//dbHelper.insertTestEntries(); // DEBUG
		} else {
			// display error
			Log.d("CourierMain.getNetworkData", "--- Network Failed ---");
		}	
		return res;
	}
	
	String user, pwd, username, prev_user;
	String login_URL, getdata_URL;

	boolean checkSameUserLogin (String userLogin, String prev_login) {
		// Проверка на совпадение введенного имени пользователя и предыдущего,
		// чтобы определить удалять старые данные или нет
		return userLogin.equals(prev_login);
	}
	
	// Показываем окно ввода имени и пароля
	private void showLogin() {

		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setCancelable(false); // запрет закрытия диалога кнопкой назад
		alert.setTitle("Введите имя и пароль");
		
		final AlertDialog alertDialog = alert.create();
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			    LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		params.weight = 1;
		
		LinearLayout.LayoutParams paramsEditTxt = new LinearLayout.LayoutParams(
			    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		paramsEditTxt.weight = 1;
		
		final LinearLayout ln = new LinearLayout(this);
		ln.setOrientation(LinearLayout.HORIZONTAL);
		ln.setDividerPadding(5);
		
		final LinearLayout lnf = new LinearLayout(this);
		lnf.setOrientation(LinearLayout.VERTICAL);
		lnf.setDividerPadding(5);
		
		// Общий Layout
		final LinearLayout lnv = new LinearLayout(this);
		lnv.setOrientation(LinearLayout.VERTICAL);
		lnv.addView(lnf);
		lnv.addView(ln);
		
		final EditText etUser = new EditText(this);
		final EditText etPwd = new EditText(this);
		final EditText etNetAddr = new EditText(this);
		etPwd.setHint("Password");
		etUser.setHint("User");
		etNetAddr.setHint("Сетевой адрес");
		etPwd.setLayoutParams(paramsEditTxt);
		etUser.setLayoutParams(paramsEditTxt);
		etNetAddr.setLayoutParams(paramsEditTxt);
		
		lnf.addView(etNetAddr);
		etNetAddr.setVisibility(EditText.INVISIBLE);
		
		etPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); // Ввод пароля скрыт
		
		Button btnOk = new Button(this);

		btnOk.setLayoutParams(params);
		Button btnCancel = new Button(this);
		btnCancel.setLayoutParams(params);
		Button btnSet = new Button(this);
		btnSet.setLayoutParams(params);
		btnOk.setText("Ok");
		btnCancel.setText("Выход");
		btnSet.setText("Настройка сети");
		ln.addView(btnOk);
		ln.addView(btnCancel);
		ln.addView(btnSet);
		
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				user = etUser.getText().toString().trim();
				pwd = etPwd.getText().toString().trim();
/*				if (! checkSameUserLogin(user)) {
					// Новый логин
					//dbHelper.deleteAllOrders();
				}*/
				Log.d("CourierMain", "--- In show login Ok ---");
				//dbHelper.open();
				
				// Сохранение сетевого адреса
				if ((etNetAddr.getVisibility() == EditText.VISIBLE)) {
					int lenAddr = etNetAddr.getText().toString().length();
					if (lenAddr > 0) {
						// Сохраняем введенный сетевой адрес
						SharedPreferences sharedAppConfig;
						sharedAppConfig = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
						Editor ed = sharedAppConfig.edit();
						String netAddr = etNetAddr.getText().toString();
						ed.putString(APPCFG_ADDR_URL, netAddr);
						ed.putString(APPCFG_LOGIN_URL, netAddr + "/data/login.php");
						ed.putString(APPCFG_GETDATA_URL, netAddr + "/data/data.php");
						ed.commit();
						
				    	login_URL = sharedAppConfig.getString(APPCFG_LOGIN_URL, "");
				    	getdata_URL = sharedAppConfig.getString(APPCFG_GETDATA_URL, "");
						Log.d("CourierMain", "SAVE NETWORK addr = " + netAddr + " login_URL = " + login_URL + " getdata_URL = " + getdata_URL);
					}
				}
				
				if (! checkSameUserLogin(user, prev_user)) {
					// Новый логин отличается от предыдущего, удаляем локальные данные
					Log.d("CourierMain", "LOGIN DIFFER, DELETE LOCAL DATA, OLD LOGIN = " + prev_user + " new login = " + user);
					dbHelper.deleteAllOrders();
					// и сохраняем новый логин для сравнения в следующем сеансе работы программы
					SharedPreferences sharedAppConfig;
					sharedAppConfig = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
					Editor ed = sharedAppConfig.edit();
					ed.putString(APPCFG_LOGIN, user);
					ed.commit();
				}
				
				int netRes = getNetworkData(nwork, user, pwd);
				if (netRes >= 0) {
					
					displayListView();
					doTimerTask();
					
					alertDialog.cancel(); // или dismiss() ?
				} else if (netRes == -1) {
					Toast.makeText(getApplicationContext(), "Ошибка сети",
							Toast.LENGTH_LONG).show();
				} // else if (netRes == -2) { 
				else {
					Toast.makeText(getApplicationContext(), "Неправильный логин или пароль",
							Toast.LENGTH_LONG).show();
				}
					
			}
		});
		
		// Настройка сетевого адреса
		btnSet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SharedPreferences sharedAppConfig;
				sharedAppConfig = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
				etNetAddr.setVisibility(EditText.VISIBLE);
				etNetAddr.setText(sharedAppConfig.getString(APPCFG_ADDR_URL, ""));
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish(); // переделать чтобы выходил без exception
				//alert.setCancelable(true);
				// onBackPressed();
				//alertDialog.cancel();
			}
			
		});
		
		
		lnf.addView(etUser);
		lnf.addView(etPwd);
		
		alertDialog.setView(lnv);

/*
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				user = etUser.getText().toString().trim();
				pwd = etPwd.getText().toString().trim();
				if (! checkSameUserLogin(user)) {
					// Новый логин
					//dbHelper.deleteAllOrders();
				}
				Log.d("CourierMain", "--- In show login Ok ---");
				//dbHelper.open();
				int netRes = getNetworkData(nwork, user, pwd);
				if (netRes >= 0) {
					displayListView();
					doTimerTask();
				} else if (netRes == -1) {
					Toast.makeText(getApplicationContext(), "Ошибка сети",
							Toast.LENGTH_LONG).show();
					displayListView();
					doTimerTask();
				} else if (netRes == -2) {
					Toast.makeText(getApplicationContext(), "Неправильный логин или пароль",
							Toast.LENGTH_LONG).show();
					finish(); // Выходим из приложения
				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
						Toast.makeText(getApplicationContext(), "Вход отменен",
								Toast.LENGTH_LONG).show();
					}
				});
		
/*		alert.setNeutralButton("Настройки", 
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Log.d("CourierMain", "--- In SETTINGS кнопка Set ---");


			}
		});*/
		
		
		alertDialog.show();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.courier_main, menu);
		return true;
	}
	

	private void displayListView() {

		cursor = dbHelper.fetchOrders(0);
		
		// The desired columns to be bound
		String[] columns = new String[] {
				OrderDbAdapter.KEY_recType,
				OrderDbAdapter.KEY_OSorDNorEMP,
				OrderDbAdapter.KEY_inway,
				OrderDbAdapter.KEY_isready,
				OrderDbAdapter.KEY_aAddress,
				OrderDbAdapter.KEY_client,
				OrderDbAdapter.KEY_timeBE
		};

		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id.tvRecType, R.id.tvOSorDNorEmp, R.id.tvInWay, R.id.tvIsredy, 
				R.id.tvaAddress, R.id.tvClient, R.id.tvTimeBE };

		// create the adapter using the cursor pointing to the desired data
		// as well as the layout information
		dataAdapter = new MyCursorAdapter(this, R.layout.orders_info,
				cursor, columns, to, 0);
		

		listView = (ListView) findViewById(R.id.listView1);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		// Селектор
		listView.setSelector(R.drawable.selector_item);
		
		// Отметка заказа в списке
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {
				// Get the cursor, positioned to the corresponding row in the
				// result set
				Cursor cursor = (Cursor) listView.getItemAtPosition(position);
				
				// Меняем цвет выделенного элемента списка
				//listView.getChildAt(position).setBackgroundColor(Color.RED);
				
				
				// Получаем значение поля этой записи из таблицы
				// String ordersClient = cursor.getString(cursor
				//		.getColumnIndexOrThrow("client"));	
				
				// Получаем ID выбранной в списке записи
				ordersId = cursor.getLong(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_ROWID));
				Log.d("LISTITEMCLICK", Long.toString(ordersId) + " выбран идентификатор");
				
				// Запоминаем значение выбранной в списке записи
				recType_forDetail = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_recType_forDetail));
				orderDetail_aNO = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_aNo));
				tvDorder_state_ordStatus = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_ordStatus));
				tvDorder_type_ordType = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_ordType));
				tvDacash = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_aCash));
				tvDaddr_aAddress = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_aAddress));
				tvDcomp_name_client = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_client));
				tvDcontact_Cont = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_Cont));
				tvDcontact_num_ContPhone = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_ContPhone));
				tvDtimeBE = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_timeBE));
				tvDpos_num_Packs = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_Packs));
				tvDweight_Wt = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_Wt));
				tvDvol_weight_VolWt = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_VolWt));
				tvDcomment = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_Rems));
				tvLocNumItems = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_locnumitems));
				tvDIsredy = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_isready));
				tvDInway = cursor.getString(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_inway));
				
				//Toast.makeText(getApplicationContext(), ordersClient + " " + ordersId,
				//		Toast.LENGTH_SHORT).show();
				// Обновляем
				// dbHelper.updOrderCatchIt(ordersId, true);
				//tvNewAllRecs.setText(cursor.getCount());
				
				// Подсветка выбранной записи и остальных - 28.06 тест работает некорректно
/*				if (ordersId == cursor.getLong(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_ROWID))) {
					// выбранная запись - зеленым
					//llvMain.setBackgroundColor(Color.RED);
					listView.getChildAt(position).setBackgroundColor(Color.RED);
				}*/
			}
		});

/*		EditText myFilter = (EditText) findViewById(R.id.myFilter);
		myFilter.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				dataAdapter.getFilter().filter(s.toString());
			}
		});

		dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			public Cursor runQuery(CharSequence constraint) {
				return dbHelper.fetchOrdersByName(constraint.toString());
			}
		});*/
		
	    // добавляем контекстное меню к списку
	    registerForContextMenu(listView);

	}
    
	// Создание контекстного меню
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_CATCH_ORDER, 0, "Начать выполнение");
		menu.add(0, CM_RET_ORDER, 0, "Отменить выполнение");
		menu.add(0, CM_BACK_ORDER, 0, "Назад к списку");
	}

	@SuppressWarnings("deprecation")
	public boolean onContextItemSelected(MenuItem item) {
		// получаем из пункта контекстного меню данные по пункту списка
		AdapterContextMenuInfo order_acmi = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == CM_CATCH_ORDER) {
			// извлекаем id записи и обновляем соответствующую запись в БД
			dbHelper.updOrderCatchIt(order_acmi.id, orderDetail_aNO);
			// обновляем курсор
			cursor.requery();
			dataAdapter.notifyDataSetChanged();
			return true;
		}
		else if (item.getItemId() == CM_RET_ORDER) {
			// извлекаем id записи и обновляем соответствующую запись в БД
			dbHelper.updOrderCatchIt(order_acmi.id, orderDetail_aNO);
			// обновляем курсор
			cursor.requery();
			dataAdapter.notifyDataSetChanged();
			return true;	
		}
		return super.onContextItemSelected(item);
	}
	  
	  protected void onDestroy() {
		    super.onDestroy();
		    // закрываем подключение при выходе
		    stopTask();
		    dbHelper.close();
		  }

	@Override
	public void onClick(View v) {
		// Обработчик кнопок главной активити
		
		switch (v.getId()) {
		case R.id.btnAddr:
		case R.id.btnClient:
		case R.id.btnTime:
		case R.id.btnType:
			Log.d("CourierMain", "--- In SORT ---");
			dataAdapter.swapCursor(dbHelper.fetchOrders(v.getId()));
			dataAdapter.notifyDataSetChanged();
			break;
			
/*		case R.id.btnSaveSet:
			Log.d("CourierMain", "--- In SETTINGS кнопка Set ---");
			Intent intentActSet = new Intent(this, ActSettings.class);
			startActivity(intentActSet);
			break;*/
			
		case R.id.btnInWay:
			//dbHelper.vibroSignal();
			// При нажатии запись помечается но список перематывается (т.е. запись снова надо искать)
			Log.d("CourierMain", "--- In switch кнопка Еду ---");
			if (! tvDIsredy.equals("1")) { // Можно делать статус Еду только у неготового заказа и только у одного
				int catchres = dbHelper.updOrderCatchIt(ordersId, orderDetail_aNO);
				if (catchres == 1) {
					Toast.makeText(getApplicationContext(), "УСТАНОВЛЕН СТАТУС ЕДУ",
							Toast.LENGTH_LONG).show();
				} else if (catchres == 0) {
					Toast.makeText(getApplicationContext(), "СТАТУС ЕДУ СБРОШЕН",
							Toast.LENGTH_LONG).show();
				} else if (catchres == 2) {
					Toast.makeText(getApplicationContext(), "СТАТУС ЕДУ НЕ МОЖЕТ БЫТЬ УСТАНОВЛЕН БОЛЬШЕ ЧЕМ У ОДНОЙ ЗАПИСИ",
							Toast.LENGTH_LONG).show();
				}
				// обновляем курсор
				cursor.requery();
				dataAdapter.swapCursor(dbHelper.fetchOrders(0));
				dataAdapter.notifyDataSetChanged();
				
				// Передача данных на сервер
				if (catchres != 2) {
					Log.d("THREAD", Thread.currentThread().getName());
					Thread tInWaySender = new Thread(new Runnable() {
						public void run() {
							String[] snddata = { orderDetail_aNO, "go", getDateTimeEvent (0), "" };
							// Здесь надо поймать -1 от nwork.sendDataGRV если была ошибка передачи,
							// сохранить snddata в хранилище чтобы потом попытаться вновь отправить все неотправленное
							int sendResult = nwork.sendDataGRV(dbHelper, user, pwd,
									login_URL, getdata_URL, snddata);
							if (sendResult == -1) {
								// Нет сети - сохраняем данные snddata в оффлайн хранилище
								dbHelper.saveSnddata("courLog", snddata);
							}
							Log.d("THREAD", Thread.currentThread().getName());
						}
					});
					tInWaySender.start();
				}
			} else {
				Log.d("CourierMain", "--- ЕДУ НЕЛЬЗЯ УСТАНОВИТЬ ДЛЯ ГОТОВОГО ЗАКАЗА ---");
				Toast.makeText(getApplicationContext(), "ЕДУ НЕЛЬЗЯ УСТАНОВИТЬ ДЛЯ СТАТУСА Ок",
						Toast.LENGTH_LONG).show();
			}
			break;
			
		case R.id.btnOk:
			Log.d("CourierMain", "--- In switch кнопка Ок ordersId = " + ordersId);
			// После проверки надо изменить значение переменной tvDIsredy (чтобы не перещелкивать текущую запись для повтороного изменения) 
			if (! recType_forDetail.equals("1")) { // на накладных кнопка Ок ничего не делает
				if (! tvDIsredy.equals("1")) { // Если статус текущей записи не Ок 
					dbHelper.updOrderIsRedy(ordersId, true);
					Toast.makeText(getApplicationContext(), "УСТАНОВЛЕН СТАТУС Ок",
							Toast.LENGTH_LONG).show();
					tvDIsredy = "1";
					// После установки статуса Ок надо сбросить статус Еду - 
					// FIX он сбрасывается в updOrderIsRedy
					if (! tvDInway.equals("0")) {
						//int catchres = dbHelper.updOrderCatchIt(ordersId, orderDetail_aNO);
					}
				} else {
					dbHelper.updOrderIsRedy(ordersId, false);
					Toast.makeText(getApplicationContext(), "СТАТУС Ок СБРОШЕН",
							Toast.LENGTH_LONG).show();
					tvDIsredy = "0";
				} 
	
				// обновляем курсор
				cursor.requery();
				dataAdapter.swapCursor(dbHelper.fetchOrders(0));
				dataAdapter.notifyDataSetChanged();
				
				// Передача данных на сервер				
				Log.d("THREAD", Thread.currentThread().getName());
				Thread tInOkSender = new Thread(new Runnable() {
					public void run() {
						String[] snddata = { orderDetail_aNO, "ready", getDateTimeEvent (0), "" };
						int sendResult = nwork.sendDataGRV(dbHelper, user, pwd,
								login_URL, getdata_URL, snddata);
						if (sendResult == -1) {
							// Нет сети - сохраняем данные snddata в оффлайн хранилище
							dbHelper.saveSnddata("courLog", snddata);
						}
						Log.d("THREAD", Thread.currentThread().getName());
					}
				});
				tInOkSender.start();
			} else {
				Toast.makeText(getApplicationContext(), "Для НАКЛАДНЫХ не применимо",
						Toast.LENGTH_LONG).show();
			}
			break;
			
		case R.id.btnPod:
			// работает только для Накладных, recType_forDetail.equals("1")
			Log.d("CourierMain", "--- In switch кнопка ПОД ---");
			if (recType_forDetail.equals("1")) {
				Intent intentPOD = new Intent(this, ActPod.class);
				intentPOD.putExtra("ordersid", ordersId);
				intentPOD.putExtra("tvDorder_num", orderDetail_aNO);
				startActivityForResult(intentPOD, ARC_POD);
				// Возвращаемое значение обрабатывается в onActivityResult
			} else {
				Toast.makeText(getApplicationContext(), "Для ЗАКАЗОВ и СЧЕТОВ не применимо",
						Toast.LENGTH_LONG).show();
			}
			break;
			
		case R.id.btnDetail:
			Log.d("CourierMain", "--- In switch кнопка ПОДРОБНО --- тип " + recType_forDetail);
			// обновляем поле isredy
			// dbHelper.updOrderIsRedy(ordersId);
			dbHelper.updOrderIsView(ordersId);
/*			cursor.requery();
			dataAdapter.swapCursor(dbHelper.fetchModOrders());
			dataAdapter.notifyDataSetChanged();*/
			if (recType_forDetail.equals("0")) {
				// Детали заказа
				Intent intent = new Intent(this, ActOrderDetail.class);
			
				intent.putExtra("tvDorder_num", orderDetail_aNO);
				intent.putExtra("tvDorder_state_ordStatus", tvDorder_state_ordStatus);
				// intent.putExtra("tvDorder_type_ordType", tvDorder_type_ordType); зачеркнуто в ТЗ
				intent.putExtra("tvDacash", tvDacash);
				intent.putExtra("tvDaddr_aAddress", tvDaddr_aAddress);
				intent.putExtra("tvDcomp_name_client", tvDcomp_name_client);
				intent.putExtra("tvDcontact_Cont", tvDcontact_Cont);
				intent.putExtra("tvDcontact_num_ContPhone", tvDcontact_num_ContPhone);
				intent.putExtra("tvDtimeBE", tvDtimeBE);
				intent.putExtra("tvDpos_num_Packs", tvDpos_num_Packs);
				intent.putExtra("tvDweight_Wt", tvDweight_Wt);
				intent.putExtra("tvDvol_weight_VolWt", tvDvol_weight_VolWt);
				intent.putExtra("tvDcomment", tvDcomment);
				startActivity(intent);
			} else if (recType_forDetail.equals("1")) {
				// Детали накладной
				Intent intent = new Intent(this, ActDlvDetail.class);
				
				intent.putExtra("tvDorder_num", orderDetail_aNO);
				intent.putExtra("tvDorder_state_ordStatus", tvDorder_state_ordStatus);
				intent.putExtra("tvDorder_type_ordType", tvDorder_type_ordType);
				intent.putExtra("tvDacash", tvDacash);
				intent.putExtra("tvDaddr_aAddress", tvDaddr_aAddress);
				intent.putExtra("tvDcomp_name_client", tvDcomp_name_client);
				intent.putExtra("tvDcontact_Cont", tvDcontact_Cont);
				intent.putExtra("tvDcontact_num_ContPhone", tvDcontact_num_ContPhone);
				intent.putExtra("tvDtimeBE", tvDtimeBE);
				intent.putExtra("tvDpos_num_Packs", tvDpos_num_Packs);
				intent.putExtra("tvDweight_Wt", tvDweight_Wt);
				intent.putExtra("tvDvol_weight_VolWt", tvDvol_weight_VolWt);
				intent.putExtra("tvDcomment", tvDcomment);
				startActivity(intent);
			}
			
			cursor.requery();
			//dataAdapter.swapCursor(dbHelper.fetchModOrders());
			//dataAdapter.swapCursor(cursor);
			dataAdapter.notifyDataSetChanged();
			
			// Передача данных на сервер				
			Log.d("THREAD", Thread.currentThread().getName());
			Thread tDetailSender = new Thread(new Runnable() {
				public void run() {
					String[] snddata = { orderDetail_aNO, "vieword", getDateTimeEvent (0), "" };
					int sendResult = nwork.sendDataGRV(dbHelper, user, pwd,
							login_URL, getdata_URL, snddata);
					if (sendResult == -1) {
						// Нет сети - сохраняем данные snddata в оффлайн хранилище
						dbHelper.saveSnddata("courLog", snddata);
					}
					
					Log.d("THREAD", Thread.currentThread().getName());
				}
			});
			tDetailSender.start();
			tvNewRecs.setText(Integer.toString(dbHelper.getNewCountOrd()));
			Log.d("DETAIL_KEY", "--- tvDorder_num = " + orderDetail_aNO);
			break;
			
		case R.id.btnNumItems:
				// Кол-во отправлений
			if (ordersId != 0) {
				Intent intent = new Intent(this, ActNumItems.class);
				
				intent.putExtra("tvLocNumItems", tvLocNumItems);
				intent.putExtra("ordersid", ordersId);
				startActivityForResult(intent, ARC_NUMITEMS);
			}
			break;
		
		case R.id.btnAll:
			// Отметить все записи как просмотренные
			dbHelper.updAllView();
			cursor.requery();
			dataAdapter.swapCursor(dbHelper.fetchOrders(0));
			dataAdapter.notifyDataSetChanged();
			tvNewRecs.setText(Integer.toString(dbHelper.getNewCountOrd()));
			Toast.makeText(getApplicationContext(), "Все записи отмечены как просмотренные",
					Toast.LENGTH_LONG).show();
			break;
			
		case R.id.btnExit:
				finish(); // Выходим из приложения
			break;
		default:
			break;
		}
		
	} // End onClick
	
	// Автообновление данных с сервера
	TimerTask mTimerTask;
	final Handler handler = new Handler();
	Timer t = new Timer();
	final int TIMER_START = 3000; // задержка перед запуском мсек
	final int TIMER_PERIOD = 180000; // период повтора мсек
	
	// Используем отдельный поток
	public void doTimerTask() {
		
		mTimerTask = new TimerTask() {
			
			@Override
			public void run() {
				handler.post(new Runnable() {

					@Override
					public void run() {
						// Здесь код обновления по таймеру
						Log.d("TIMER", "TimerTask run");
						
						// dbHelper.deleteAllOrders(); теперь не удаляю поскольку идет проверка перед созданием записи в NetWorker
						int cntnewrecs = nwork.getData(dbHelper, user, pwd, login_URL, getdata_URL);
						
						if (cntnewrecs >= 0) { // Связь с сервером ОК
							imgvSrvOff.setVisibility(View.INVISIBLE);
							imgvSrvOn.setVisibility(View.VISIBLE);
							
							cursor.requery();
							int cntallrecs = cursor.getCount();
							int cntnewnotviewrecs = dbHelper.getNewCountOrd();
							//tvNewAllRecs.setText(Integer.toString(cntnewrecs) + "/" + Integer.toString(cntallrecs));
							tvAllRecs.setText(Integer.toString(cntallrecs));
							tvNewRecs.setText(Integer.toString(cntnewnotviewrecs));
							tvRefrTime.setText(getDateTimeEvent(1));
							Log.d("TIMER", "Count records from cursor new/all " + cntnewnotviewrecs + "/" + cntallrecs);
							dataAdapter.swapCursor(dbHelper.fetchOrders(0));
							dataAdapter.notifyDataSetChanged();
							
							sendOfflineData(); // отправляем оффлайн данные
						} else { // Проблемы связи с сервером
							imgvSrvOff.setVisibility(View.VISIBLE);
							imgvSrvOn.setVisibility(View.INVISIBLE);
							//tvNewAllRecs.setText("");
							tvNewRecs.setText("");
							tvAllRecs.setText("");
							tvRefrTime.setText(getDateTimeEvent(1));
						}
					}
					
				});
				
			}
		};
		
		t.schedule(mTimerTask, TIMER_START, TIMER_PERIOD);
		
	}
	
	public void stopTask() {
		if (mTimerTask != null) {
			Log.d("TIMER", "TimerTask canceled");
			mTimerTask.cancel();
		}
	}
	
	String getDateTimeEvent (int retFormat) {
		// Возвращает дату и время в формате
		final String DTFORMAT; // = "yyyyMMdd HH:mm";
		switch (retFormat) {
		case 0:
			DTFORMAT = "yyyyMMdd HH:mm";
			break;
		case 1:
			DTFORMAT = "HH:mm";
			break;

		default:
			DTFORMAT = "HH:mm";
			break;
		}
		Date c = Calendar.getInstance().getTime(); 
		TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				DTFORMAT);
		dateFormat.setTimeZone(tz);
		return dateFormat.format(c);
	}
	
	// Отправка оффлайн данных 
	void sendOfflineData() {
		final List<String[]> dataList = dbHelper.getSnddata();
		if ( dataList != null) {
					
			for (String[] rowData : dataList) {
				
				Log.d("CourierMain", "Offline send = " + rowData[0] + rowData[1] + rowData[2] + rowData[3] + " id=" + rowData[4] + " type=" + rowData[5]);
				if ((rowData[5]).equals("courLog")) {
					// String[] snddata = { orderDetail_aNO, event, tdd, "" }
					int sendResult = nwork.sendDataGRV(dbHelper, user, pwd,
							login_URL, getdata_URL, rowData);
					if (sendResult >= 0) { // отправка на сервер успешно, удалить оффлайн запись
						dbHelper.deleteOfflineData(rowData[4]);
					}
				} else if ((rowData[5]).equals("SetPOD")) {
					// String[] snddata = { wb_no, p_d_in, tdd, rcpn };
					int sendResult = nwork.sendData(this.dbHelper, this.user, this.pwd,
							this.login_URL, this.getdata_URL, rowData);
					if (sendResult >= 0) {// отправка на сервер успешно, удалить оффлайн запись
						dbHelper.deleteOfflineData(rowData[4]);
					}
				} else {
					Log.d("CourierMain", "WARNING sendOfflineData unknown type for sending=" + rowData[5]);
				}
			}
		}
	}
	
}

