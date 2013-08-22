package com.flippost.courier;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;

public class OrderDbAdapter {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_aNo = "aNo";
	public static final String KEY_displayNo = "displayNo";
	public static final String KEY_aCash = "aCash";
	public static final String KEY_aAddress = "aAddress";
	public static final String KEY_client = "client";
	public static final String KEY_timeB = "timeB";
	public static final String KEY_timeE = "timeE";
	public static final String KEY_tdd = "tdd";
	public static final String KEY_Cont = "Cont";
	public static final String KEY_ContPhone = "ContPhone";
	public static final String KEY_Packs = "Packs";
	public static final String KEY_Wt = "Wt";
	public static final String KEY_VolWt = "VolWt";
	public static final String KEY_Rems = "Rems";
	public static final String KEY_ordStatus = "ordStatus";
	public static final String KEY_ordType = "ordType";
	public static final String KEY_recType = "recType";
	public static final String KEY_recType_forDetail = "recType_forDetail";
	public static final String KEY_isready = "isready";
	public static final String KEY_inway = "inway";
	public static final String KEY_isview = "isview";
	public static final String KEY_rcpn = "rcpn";
	public static final String KEY_comment = "comment";

	// Поля для запроса с подстановкой
	public static final String KEY_OSorDNorEMP = "OSorDNorEMP";
	public static final String KEY_timeBE = "timeBE";

	// Поле для локального хранения кол-ва посылок
	public static final String KEY_locnumitems = "locnumitems";

	// состояние сортировки
	private int sortType = -1; // id кнопки сортировки
	private boolean sortOrder = true; // true - asc, false - desc

	static final String SQL_CASE = "select _id, " + "case "
			+ "when recType = '0' then 'Заказ' "
			+ "when recType = '1' then 'Накладная' "
			+ "when recType = '2' then 'Счет' " + "end as recType, " + "case "
			+ "when recType = '0' then ordStatus "
			+ "when recType = '1' then displayNo "
			+ "when recType = '2' then	'' " + "else 'Not defined' "
			+ "end as OSorDNorEMP, " + "case " + "when inWay = '0' then '0' "
			+ "else 'Еду' " + "end as inway, " + "isready, "
			+ "aAddress, client, " + "case "
			+ "when recType = '0' then ' с ' || timeB || ' по ' || timeE "
			+ "else '' " + "end as timeBE " + ", recType as recType_forDetail "
			+ ", aNo " + ", ordStatus " + ", ordType " + ", aCash " + ", Cont "
			+ ", ContPhone " + ", Packs " + ", Wt " + ", VolWt " + ", Rems "
			+ ", locnumitems " + ", isview " + ", tdd " + "from Orders ";

	private static final String TAG = "ORDERDBADAPTER";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String DATABASE_NAME = "Courier.db";
	private static final String SQLITE_TABLE = "Orders";
	private static final String OFFLINE_TABLE = "snddata";
	private static final int DATABASE_VERSION = 5;

	private final Context mCtx;

	// Структура таблицы
	private static final String DATABASE_CREATE = "CREATE TABLE if not exists "
			+ SQLITE_TABLE + " (" + KEY_ROWID
			+ " integer PRIMARY KEY autoincrement," + KEY_aNo + ","
			+ KEY_displayNo + "," + KEY_aCash + "," + KEY_aAddress + ","
			+ KEY_client + "," + KEY_timeB + "," + KEY_timeE + "," + KEY_tdd
			+ "," + KEY_Cont + "," + KEY_ContPhone + "," + KEY_Packs + ","
			+ KEY_Wt + "," + KEY_VolWt + "," + KEY_Rems + "," + KEY_ordStatus
			+ "," + KEY_ordType + "," + KEY_recType + "," + KEY_isready + ","
			+ KEY_inway + "," + KEY_isview + "," + KEY_rcpn + ","
			+ KEY_locnumitems + " default 0 " + "); ";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.w(TAG, DATABASE_CREATE);
			db.execSQL(DATABASE_CREATE);
			db.execSQL("CREATE TABLE if not exists snddata (_id integer PRIMARY KEY autoincrement, sndtype, f1, f2, f3, f4)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
			db.execSQL("DROP TABLE IF EXISTS snddata");
			onCreate(db);
		}
	}

	public OrderDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public OrderDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		if (mDbHelper != null) {
			mDbHelper.close();
		}
	}

	public void vibroSignal(){
		Log.d(TAG, "vibroSignal");
		Vibrator v = (Vibrator) mCtx.getSystemService(Context.VIBRATOR_SERVICE);
		
		long[] pattern = {0, 200, 100, 200, 100, 200};
		v.vibrate(pattern, -1);		
	}

	void doNotify() {
		Log.w(TAG, "--start notify");
		NotificationManager nm;
		nm = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
	
		// 3-я часть
		/*Intent intent = new Intent(mCtx, CourierMain.class);
		intent.putExtra("name", "somefile");
		PendingIntent pIntent = PendingIntent.getActivity(mCtx, 0, intent, 0);*/

		// 1-я часть
		android.support.v4.app.NotificationCompat.Builder nb = new NotificationCompat.Builder(mCtx)
			.setContentTitle("ФЛИППОСТ")
			.setContentText("Новый заказ")
			.setSmallIcon(R.drawable.ic_launcher_fp)
			.setNumber(getNewCountOrd()+1)//+1???
			.setDefaults(Notification.DEFAULT_ALL);

		Notification notif = nb.build();
		notif.flags |= Notification.FLAG_INSISTENT;

		// отправляем
		nm.notify(1, notif);
	}
	  
	// Создание записи в таблице
	public long createOrder(String aNo, String displayNo, String aCash,
			String aAddress, String client, String timeB, String timeE,
			String tdd, String Cont, String ContPhone, String Packs, String Wt,
			String VolWt, String Rems, String ordStatus, String ordType,
			String recType, String isready, String inway, String isview,
			String rcpn) {

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_aNo, aNo);
		initialValues.put(KEY_displayNo, displayNo);
		initialValues.put(KEY_aCash, aCash);
		initialValues.put(KEY_aAddress, aAddress);
		initialValues.put(KEY_client, client);
		initialValues.put(KEY_timeB, timeB);
		initialValues.put(KEY_timeE, timeE);
		initialValues.put(KEY_tdd, tdd);
		initialValues.put(KEY_Cont, Cont);
		initialValues.put(KEY_ContPhone, ContPhone);
		initialValues.put(KEY_Packs, Packs);
		initialValues.put(KEY_Wt, Wt);
		initialValues.put(KEY_VolWt, VolWt);
		initialValues.put(KEY_Rems, Rems);
		initialValues.put(KEY_ordStatus, ordStatus);
		initialValues.put(KEY_ordType, ordType);
		initialValues.put(KEY_recType, recType);
		initialValues.put(KEY_isready, isready);
		initialValues.put(KEY_inway, inway);
		initialValues.put(KEY_isview, isview);
		initialValues.put(KEY_rcpn, rcpn);

		//vibroSignal();
		doNotify();
		
		return mDb.insert(SQLITE_TABLE, null, initialValues);
	}

	public int updateOrder(String aNo, String displayNo, String aCash,
			String aAddress, String client, String timeB, String timeE,
			String tdd, String Cont, String ContPhone, String Packs, String Wt,
			String VolWt, String Rems, String ordStatus) {
		ContentValues updValues = new ContentValues();
		// initialValues.put(KEY_aNo, aNo);
		updValues.put(KEY_displayNo, displayNo);
		updValues.put(KEY_aCash, aCash);
		updValues.put(KEY_aAddress, aAddress);
		updValues.put(KEY_client, client);
		updValues.put(KEY_timeB, timeB);
		updValues.put(KEY_timeE, timeE);
		updValues.put(KEY_tdd, tdd);
		updValues.put(KEY_Cont, Cont);
		updValues.put(KEY_ContPhone, ContPhone);
		updValues.put(KEY_Packs, Packs);
		updValues.put(KEY_Wt, Wt);
		updValues.put(KEY_VolWt, VolWt);
		updValues.put(KEY_Rems, Rems);
		updValues.put(KEY_ordStatus, ordStatus);

		return mDb.update(SQLITE_TABLE, updValues, KEY_aNo + "=?",
				new String[] { aNo });
	}

	public boolean deleteAllOrders() {

		int doneDelete = 0;
		doneDelete = mDb.delete(SQLITE_TABLE, null, null);
		Log.w(TAG, Integer.toString(doneDelete)
				+ "--- Deleted records by deleteAllOrders()");
		return doneDelete > 0;

	}

	// sql-строка сортировки: order by ...
	private String getSorter(int sortType) {
		String sqlSorter = " order by ";

		// установить порядок сортировки
		if (sortType != 0)
			if (sortType != this.sortType) {
				this.sortType = sortType;
				sortOrder = true;
			} else {
				sortOrder = !sortOrder;
			}

		switch (this.sortType) {
		case R.id.btnAddr: {
			if (sortOrder) {
				sqlSorter += KEY_aAddress + " asc, " + KEY_client + " asc";
			} else {
				sqlSorter += KEY_aAddress + " desc, " + KEY_client + " desc";

			}
			break;
		}
		case R.id.btnClient: {
			if (sortOrder) {
				sqlSorter += KEY_client + " asc";
			} else {
				sqlSorter += KEY_client + " desc";

			}
			break;
		}
		case R.id.btnTime: {
			if (sortOrder) {
				sqlSorter += KEY_timeB + " asc";
			} else {
				sqlSorter += KEY_timeB + " desc";

			}
			break;
		}
		case R.id.btnType: {
			if (sortOrder) {
				sqlSorter += KEY_ordType + " asc, " + KEY_aAddress + " asc";
			} else {
				sqlSorter += KEY_ordType + " desc, " + KEY_aAddress + " desc";

			}
			break;
		}
		default:
			sqlSorter = "";
		}

		return sqlSorter;
	}

	public Cursor fetchOrders(int sortType) {
		String sorter;
		sorter = getSorter(sortType);

		Cursor mCursor = mDb.rawQuery(SQL_CASE + sorter, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// Пока не делаю
	public Cursor fetchOrdersByName(String inputText) throws SQLException {
		Log.w(TAG, inputText);

		return null;
	}

	// Взятие/возврат выбранного заказа в работу
	int updOrderCatchIt(long rowid, String aNo) // long rowid, boolean isCatch
	{
		ContentValues cv = new ContentValues();
		int CATCH_OK = 1; // заказ взят
		int CATCH_RES = 0; // заказ сброшен
		int CATCHED_OTHER = 2; // другая запись со статусом взята
		int res = 3;
		/*
		 * if (isCatch) { cv.put(KEY_inway, "1"); Log.d(TAG,
		 * "Заказ будет взят"); } else { cv.put(KEY_inway, "0"); Log.d(TAG,
		 * "Заказ будет сброшен"); }
		 */
		Cursor mCursor = mDb.rawQuery("select aNo from orders where inway = ?",
				new String[] { "1" });
		if (mCursor != null) {
			if (!mCursor.moveToFirst()) { // не найдены другие записи у которых
											// ЕДУ = 1
				cv.put(KEY_inway, "1");
				Log.d(TAG, "Заказ будет взят");
				res = CATCH_OK;
				mDb.update(SQLITE_TABLE, cv, KEY_ROWID + "=?",
						new String[] { Long.toString(rowid) });
				Log.d(TAG, "Updated record rowid = " + rowid + " and res = "
						+ res);
			} else { // найдена запись у которой ЕДУ = 1
				String selaNo = mCursor.getString(mCursor
						.getColumnIndexOrThrow(OrderDbAdapter.KEY_aNo));

				if (selaNo.equals(aNo)) { // если она текущая сбрасываем ЕДУ = 0
											// или возвращаем CATCHED_OTHER если
											// не текущая
					cv.put(KEY_inway, "0");
					Log.d(TAG, "Заказ будет сброшен");
					res = CATCH_RES;
					mDb.update(SQLITE_TABLE, cv, KEY_ROWID + "=?",
							new String[] { Long.toString(rowid) });
					Log.d(TAG, "Updated record rowid = " + rowid
							+ " and res = " + res);
				} else {
					res = CATCHED_OTHER;
				}
			}
		}
		return res;
	}

	// Обновление флага что заказ просмотрен (не просмотрен а готов) (Поле Ок)
	int updOrderIsRedy(long rowid, boolean isready) {
		ContentValues cv = new ContentValues();
		if (isready) {
			cv.put(KEY_isready, "1");
			Log.d("ORDERDBADAPTER", "Статус ОК будет установлен");
		} else {
			cv.put(KEY_isready, "0");
			Log.d("ORDERDBADAPTER", "Статус ОК будет сброшен");
		}
		cv.put(KEY_inway, "0"); // сбрасываем статус Еду (при нажатии Ок)
		int rowsUpd = mDb.update(SQLITE_TABLE, cv, KEY_ROWID + "=?",
				new String[] { Long.toString(rowid) });
		return rowsUpd;
	}

	int updLocNumItems(long rowid, String numItems) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_locnumitems, numItems);

		int rowsUpd = mDb.update(SQLITE_TABLE, cv, KEY_ROWID + "=?",
				new String[] { Long.toString(rowid) });
		return rowsUpd;
	}

	// Обновление поля заказ просмотрен (при нажатии на кнопку Подробно)
	int updOrderIsView(long rowid) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_isview, "1");
		int rowsUpd = mDb.update(SQLITE_TABLE, cv, KEY_ROWID + "=?",
				new String[] { Long.toString(rowid) });
		// mDb.update(table, values, whereClause, whereArgs)
		return rowsUpd;
	}

	int updPodTime(long rowid, String tdd) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_tdd, tdd);
		int rowsUpd = mDb.update(SQLITE_TABLE, cv, KEY_ROWID + "=?",
				new String[] { Long.toString(rowid) });
		return rowsUpd;
	}

	// Пометка всех заказов как просмотренных
	int updAllView() {
		ContentValues cv = new ContentValues();
		cv.put(KEY_isview, "1");
		int rowsUpd = mDb.update(SQLITE_TABLE, cv, null, null);
		return rowsUpd;
	}

	// Сохранение данных для отправки при отсутствии сети
	void saveSnddata(String sndtype, String[] snddata) {
		// snddata
		ContentValues cv = new ContentValues();
		cv.put("sndtype", sndtype);
		cv.put("f1", snddata[0]);
		cv.put("f2", snddata[1]);
		cv.put("f3", snddata[2]);
		cv.put("f4", snddata[3]);
		mDb.insert(OFFLINE_TABLE, null, cv);
	}

	// Получаем оффлайн данные для отправки на сервер
	List<String[]> getSnddata() {
		List<String[]> strSnddata = new ArrayList<String[]>();
		String SQL = "select _id, sndtype, f1,f2,f3,f4 from snddata";
		Cursor mCursor = mDb.rawQuery(SQL, null);

		if (mCursor.moveToFirst()) {
			// String [] data = new String [6];
			do { // получаем {f1, f2, f3, f4} == { orderDetail_aNO или wb_no,
					// event или p_d_in, tdd или eventtime, rcpn или rem = "" }
				String[] data = new String[6];
				data[0] = mCursor.getString(2); // wb_no или ano
				data[1] = mCursor.getString(3); // p_d_in (дата) или event
				data[2] = mCursor.getString(4); // tdd или eventtime
				data[3] = mCursor.getString(5); // rcpn или rem ""
				data[4] = mCursor.getString(0); // rowid для удаления при
												// успешной отправке
				data[5] = mCursor.getString(1); // для определения типа -
												// sdntype = SetPOD или courLog
				strSnddata.add(data);
			} while (mCursor.moveToNext());
		} else {
			strSnddata = null;
		}
		return strSnddata;
	}

	// Удаление оффлайн данных после отправки на сервер
	boolean deleteOfflineData(String rowid) {
		int doneDelete = 0;
		doneDelete = mDb.delete(OFFLINE_TABLE, "_id=?", new String[] { rowid });
		Log.w(TAG, Integer.toString(doneDelete)
				+ "--- Deleted records by deleteOfflineData(rowid) ---");
		return doneDelete >= 0;
	}

	// Необходимо для определения есть ли такой заказ aNo локально или это новая
	// запись которую надо сохранить локально
	boolean isNewOrder(String aNo) {
		boolean res = true; // запись новая локально нет
		String SQL = "select aNo from orders where aNo = ?";
		Cursor mCursor = mDb.rawQuery(SQL, new String[] { aNo });

		if (mCursor.moveToFirst()) {
			res = false; // такая запись есть локально
		}
		Log.d("ORDERDBADAPTER", "isNewOrder record present is " + res);
		return res;
	}

	// Удаление несуществующих на сервере записей
	boolean deleteNotExistOrd(String aNoListOnServer) {
		boolean cntDel = false;
		String SQLDEL = "delete from orders where aNo not in ("
				+ aNoListOnServer + ")";
		Cursor mCursor = mDb.rawQuery(SQLDEL, null);
		if (mCursor.moveToFirst()) {
			cntDel = true;
			Log.d("ORDERDBADAPTER", "record deleted");
		}
		Log.d("ORDERDBADAPTER",
				"not exist record deleted " + mCursor.getCount());
		return cntDel;
	}

	int getNewCountOrd() {
		int cnt = 0;
		String SQLCNT = "select _id from orders where isview <> '1'";
		Cursor mCursor = mDb.rawQuery(SQLCNT, null);
		cnt = mCursor.getCount();
		Log.d("ORDERDBADAPTER",
				"Count new recs = " + Integer.toString(mCursor.getCount()));
		return cnt;
	}

	int getCountOrd() {
		String SQLCNT = "select _id from orders";
		Cursor mCursor = mDb.rawQuery(SQLCNT, null);

		return mCursor.getCount();
	}

	// Тестовые данные
	public void insertTestEntries() {
		createOrder(
				// aNo, displayNo, aCash, aAddress, client, timeB, timeE, tdd,
				// Cont, ContPhone, Packs, Wt, VolWt, Rems, ordStatus, ordType,
				// recType, isready, inway, isview, rcpn)
				"1509-9545", "1509-9545", "NULL", "4-Й ЛИХАЧЁВСКИЙ ПЕР. 4",
				"ЭСАБ (4-Й ЛИХ)", "NULL", "NULL", "10:15", "МОМОТ",
				"89857274131", "2", "2.7", "0", "Документы", "NULL", "NULL",
				"1", "0", "0", "0", "NULL");
		createOrder("3988", "3988", "NULL", "АДМИРАЛА МАКАРОВА, Д.8",
				"ЮПС ЭС СИ ЭС", "NULL", "NULL", "16:42", "ЮПС Эс Си Эс",
				"785-71-50", "1", "0.1", "0", "", "NULL", "NULL", "1", "0",
				"0", "0", "NULL");
		createOrder(
				"266121",
				"ЗАКАЗ",
				"NULL",
				"ВОЙКОВСКИЙ 5-Й ПР. 2",
				"БЕЗАНТ",
				"9:00",
				"17:30",
				"NULL",
				"любое",
				"720-66-38,720-66-39",
				"NULL",
				"NULL",
				"0",
				"Куда: ROV -ЭКСПРЕСС ГРУЗ   , Получатель: ООО ТТЦ Импульс ПЛЮС +, Адрес:РОСТОВ-на ДОНУ  Семашко 117а/146 оф.12, Контакт: Дьяченко Михаил, Телефон: 2462-586, Примечание отправителя: ЭКЛЗ, Примечание получателя: сч № 2555",
				"NULL", "0", "0", "0", "0", "0", "NULL");
		createOrder("37965", "37965", "NULL", "ВРУБЕЛЯ Д.12 БЦ СОКОЛ 1",
				"МАРАТЕКС", "NULL", "NULL", "12:25", "Маратекс", "89169904999",
				"1", "0.2", "0", "", "NULL", "NULL", "1", "0", "0", "0", "NULL");
		createOrder("1616-3520", "1616-3520", "NULL",
				"ЛЕНИНГРАДСКИЙ ПР-Т  Д.63 ОФ.504", "АПРИОРИ ООО", "NULL",
				"NULL", "12:05", "РЕМИЗОВА ВЛАДА ", "9067906193", "1", "18.6",
				"0", "Документы", "NULL", "NULL", "1", "0", "1", "0", "NULL");
		createOrder("1619-6322", "1619-6322", "NULL",
				"ЛЕНИНГРАДСКОЕ ШОССЕ 65 СТР5", "СИНЕЛАБЛОГИСТИКА", "NULL",
				"NULL", "16:30", "Синелаблогистика", "7893724", "2", "6", "0",
				"", "NULL", "NULL", "1", "0", "0", "0", "NULL");
		createOrder("32002431", "32002431", "NULL",
				"ЛЕНИНГРАДСКОЕ ШОССЕ Д. 71 Г", "ООО МЕТРО КЕШ ЭНД КЕРРИ",
				"NULL", "NULL", "16:10", "ООО Метро Кеш Энд Ке",
				"8-495-502-17-72", "1", "0.2", "0", "", "NULL", "NULL", "1",
				"0", "0", "0", "NULL");
		createOrder("1188354", "1188354", "NULL",
				"ПР-Д.СТАРОПЕТРОВСКИЙ  Д.7А,  КОРП.25  АП.4", "ООО БЕЛЛЬ",
				"NULL", "NULL", "12:52", "ООО Белль",
				"Сероштанова Светлана 495-", "1", "0.1", "0", "", "NULL",
				"NULL", "1", "0", "0", "0", "NULL");
		createOrder("812006317", "812006317", "NULL", "СМОЛЬНАЯ 31 КВ 79",
				"ВЕКТОРФОИЛТЕК", "NULL", "NULL", "NULL", "ТАТЬЯНА",
				"9162391516", "1", "0.1", "0", "Документы", "NULL", "NULL",
				"1", "0", "0", "0", "NULL");
		createOrder("266130", "ЗАКАЗ", "NULL", "СМОЛЬНАЯ УЛ. 14",
				"СПЕКТР-АВИА ООО", "13:30", "18:00", "NULL",
				"СВЕТЛАНА .......", "2312830", "NULL", "NULL", "NULL", "NULL",
				"NULL", "1", "0", "0", "0", "0", "NULL");
		createOrder("1286-6456", "1286-6456", "NULL", "СМОЛЬНАЯ УЛ. 20А",
				"ГЕКСАГОН ЦШК ООО", "NULL", "NULL", "15:15",
				"ЗЕЛЕНЧУК ВЛАДИМИР", "89260012941", "1", "1.08", "0",
				"деталь с возвратом", "NULL", "NULL", "1", "0", "0", "0",
				"NULL");
		createOrder("266200", "ЗАКАЗ", "NULL", "СМОЛЬНАЯ УЛ. 20А", "ГЕКСОГОН",
				"NULL", "NULL", "NULL", "Антон ..Наталья", "7887920", "NULL",
				"NULL", "NULL", "NULL", "NULL", "0", "0", "0", "0", "0", "NULL");
		createOrder("1203-7452", "1203-7452", "NULL",
				"СТАРОПЕТРОВСКИЙ ПР-Д Д. 7 А СТР. 25", "БЕЛЛЬ ООО ", "NULL",
				"NULL", "12:52", "ЗВОНКОВА", "495-641-5790", "1", "0.7", "0",
				"Документы", "NULL", "NULL", "1", "0", "0", "0", "NULL");
		createOrder("1366-5298", "1366-5298", "NULL",
				"УЛ ПРАВОБЕРЕЖНАЯ СТР 1Б", "МЕДИА МАРКТ", "NULL", "NULL",
				"15:58", "ЖИЛЯБИНА ИРИНА", "89250820980", "1", "1.26", "0",
				"ПЛАСТИКОВАЯ КАРТА", "NULL", "NULL", "1", "0", "0", "0", "NULL");
	}
}
