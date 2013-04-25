package com.informixonline.courierproto;

import com.informixonline.courierproto.OrderDbAdapter;
import com.informixonline.courierproto.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) 
public class CourierMain extends Activity implements OnClickListener {
	
	static long ordersId; // ID заказа выбранного в списке для подсветки через CustomAdapter
	
	Cursor cursor;
	ListView listView;
	
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

	// Идентификаторы контекстного меню списка
	private static final int CM_CATCH_ORDER = 0;
	private static final int CM_RET_ORDER = 1;
	private static final int CM_BACK_ORDER = 2;
	
	private OrderDbAdapter dbHelper;
	// private SimpleCursorAdapter dataAdapter;
	private MyCursorAdapter dataAdapter;
	
	// Кнопки главной активити
	Button btnAddr, btnClient, btnTime, btnInWay, btnOk, btnPod, btnDetail;
	Button btnInsertTest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_courier_main);
		
		// Показываем диалог логина и ждем ввода
		showLogin();
		
        // Выключаем проверку работы с сетью в текущем UI потоке
        StrictMode.ThreadPolicy policy = new StrictMode.
        		ThreadPolicy.Builder().permitAll().build();
        		StrictMode.setThreadPolicy(policy);

		dbHelper = new OrderDbAdapter(this);
		dbHelper.open();

		// Clean all data
		dbHelper.deleteAllOrders();
		// Add some data
		//dbHelper.insertTestEntries();
		Log.d("POST", "--- delete all orders before connect ---");
		

		
		// Кнопки отладки
		btnInsertTest = (Button)findViewById(R.id.btnInsertTest);
		btnInsertTest.setOnClickListener(this);
		// Кнопки на главной активити
		btnAddr = (Button)findViewById(R.id.btnAddr);
		btnAddr.setOnClickListener(this);
		
		btnClient = (Button)findViewById(R.id.btnClient);
		btnClient.setOnClickListener(this);
		
		btnTime = (Button)findViewById(R.id.btnTime);
		btnTime.setOnClickListener(this);
		
		btnInWay = (Button)findViewById(R.id.btnInWay);
		btnInWay.setOnClickListener(this);
		btnOk = (Button)findViewById(R.id.btnOk);
		btnOk.setOnClickListener(this);
		btnPod = (Button)findViewById(R.id.btnPod);
		btnPod.setOnClickListener(this);
		btnDetail = (Button)findViewById(R.id.btnDetail);
		btnDetail.setOnClickListener(this);
		
		// Generate ListView from SQLite Database
		// displayListView(); moved to dialog
		
	}
	
	private void getNetworkData(String user, String pwd) {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			NetWorker nwork = new NetWorker();
			Log.d("POST", "--- Network OK ---");
		    nwork.getData(dbHelper, user, pwd);
			//dbHelper.insertTestEntries();
		} else {
			// display error
			Log.d("POST", "--- Network Failed ---");
		}	
	}
	
	String user, pwd;

	private void showLogin() {
		// TODO Auto-generated method stub
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Введите имя и пароль");
		
		final LinearLayout ln = new LinearLayout(this);
		ln.setOrientation(1);
		ln.setDividerPadding(5);
		final EditText etUser = new EditText(this);
		final EditText etPwd = new EditText(this);
		etPwd.setHint("Password");
		etUser.setHint("User");
		etPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		
		ln.addView(etUser);
		ln.addView(etPwd);
		
		alert.setView(ln);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				user = etUser.getText().toString().trim();
				pwd = etPwd.getText().toString().trim();
/*				Toast.makeText(getApplicationContext(), user,
						Toast.LENGTH_SHORT).show();*/
				
				getNetworkData(user, pwd);
				displayListView();
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.courier_main, menu);
		return true;
	}
	

	private void displayListView() {

		cursor = dbHelper.fetchModOrders();

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
		
		// Отметка заказа в списке
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {
				// Get the cursor, positioned to the corresponding row in the
				// result set
				Cursor cursor = (Cursor) listView.getItemAtPosition(position);
				
				// Меняем цвет выделенного элемента списка
				// listView.getChildAt(position).setBackgroundColor(Color.BLUE);
				
				// Получаем значение поля этой записи из таблицы
				// String ordersClient = cursor.getString(cursor
				//		.getColumnIndexOrThrow("client"));	
				// Получаем ID записи
				ordersId = cursor.getLong(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_ROWID));
				Log.d("LISTITEMCLICK", Long.toString(ordersId) + " выбран идентификатор");
				
				// Запоминаем значение выбранной записи
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
				 
				//Toast.makeText(getApplicationContext(), ordersClient + " " + ordersId,
				//		Toast.LENGTH_SHORT).show();
				// Обновляем
				// dbHelper.updOrderCatchIt(ordersId, true);
				
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
			dbHelper.updOrderCatchIt(order_acmi.id, true);
			// обновляем курсор
			cursor.requery();
			dataAdapter.notifyDataSetChanged();
			return true;
		}
		else if (item.getItemId() == CM_RET_ORDER) {
			// извлекаем id записи и обновляем соответствующую запись в БД
			dbHelper.updOrderCatchIt(order_acmi.id, false);
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
		    dbHelper.close();
		  }

	@Override
	public void onClick(View v) {
		// Обработчик кнопок главной активити
		
		switch (v.getId()) {
		case R.id.btnAddr:
			Log.d("CourierMain", "--- In SORT кнопка Адрес ---");
			//dataAdapter.swapCursor(cursor).close();
			//cursor = dbHelper.fetchSortOrders(1);
			dataAdapter.swapCursor(dbHelper.fetchSortOrders(1));
			dataAdapter.notifyDataSetChanged();
			break;
			
		case R.id.btnClient:
			Log.d("CourierMain", "--- In SORT кнопка Клиент ---");
			dataAdapter.swapCursor(dbHelper.fetchSortClients(1));
			dataAdapter.notifyDataSetChanged();
			break;
			
		case R.id.btnTime:
			Log.d("CourierMain", "--- In SORT кнопка Время ---");
			dataAdapter.swapCursor(dbHelper.fetchSortTimeB(1));
			dataAdapter.notifyDataSetChanged();
			break;
			
		case R.id.btnInWay:
			// При нажатии запись помечается но список перематывается (т.е. запись снова надо искать)
			Log.d("CourierMain", "--- In switch кнопка Еду ---");
			dbHelper.updOrderCatchIt(ordersId, true);
			// обновляем курсор
			cursor.requery();
			//dataAdapter.swapCursor(dbHelper.fetchModOrders());
			dataAdapter.notifyDataSetChanged();
			break;
			
		case R.id.btnOk:
			Log.d("CourierMain", "--- In switch кнопка Ок ---");
			break;
			
		case R.id.btnPod:
			Log.d("CourierMain", "--- In switch кнопка ПОД ---");
			break;
			
		case R.id.btnDetail:
			Log.d("CourierMain", "--- In switch кнопка ПОДРОБНО --- тип " + recType_forDetail);
			// обновляем поле isredy
			dbHelper.updOrderIsRedy(ordersId);
			cursor.requery();
			dataAdapter.notifyDataSetChanged();
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
			
			Log.d("DETAIL_KEY", "--- tvDorder_num = " + orderDetail_aNO);
			break;
			
		case R.id.btnInsertTest:
			dbHelper.insertTestEntries();
			break;
		default:
			break;
		}
		
	}
}
