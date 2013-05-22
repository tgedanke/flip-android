package com.informixonline.courierproto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
	
	static final String SQL_CASE = "select _id, " +
			"case " + 
			"when recType = '0' then 'Заказ' " +
			"when recType = '1' then 'Накладная' " +
			"when recType = '2' then 'Счет' " +
		"end as recType, " +
		"case " +
			"when recType = '0' then ordStatus "+
			"when recType = '1' then displayNo "+
			"when recType = '2' then	'' "+
			"else 'Not defined' "+
		"end as OSorDNorEMP, "+
		"case "+
			"when inWay = '0' then '0' "+
			"else 'Еду' "+
		"end as inway, "+
		"isready, "+
		"aAddress, client, "+
		"case "+
			"when recType = '0' then ' с ' || timeB || ' по ' || timeE "+
			"else '' "+
		"end as timeBE "+
		", recType as recType_forDetail " +
		", aNo " +
		", ordStatus " +
		", ordType " +
		", aCash " +
		", Cont " +
		", ContPhone " +
		", Packs " +
		", Wt " +
		", VolWt " +
		", Rems " +
		", locnumitems " + 
		", isview " +
		"from Orders ";
	
	private static final String TAG = "OrdersDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String DATABASE_NAME = "Courier.db";
	private static final String SQLITE_TABLE = "Orders";
	private static final int DATABASE_VERSION = 2;

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
			+ KEY_inway + "," + KEY_isview + "," + KEY_rcpn + "," + KEY_locnumitems + " default 0 " + ");";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.w(TAG, DATABASE_CREATE);
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
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

	// Создание записи в таблице
	public long createOrder(	String aNo,
								String displayNo,
								String aCash,
								String aAddress,
								String client,
								String timeB,
								String timeE,
								String tdd,
								String Cont,
								String ContPhone,
								String Packs,
								String Wt,
								String VolWt,
								String Rems,
								String ordStatus,
								String ordType,
								String recType,
								String isready,
								String inway,
								String isview,
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


		return mDb.insert(SQLITE_TABLE, null, initialValues);
	}
	
	public boolean deleteAllOrders() {

		int doneDelete = 0;
		doneDelete = mDb.delete(SQLITE_TABLE, null, null);
		Log.w(TAG, Integer.toString(doneDelete));
		return doneDelete > 0;

	}
	
	// Пока не делаю
	public Cursor fetchOrdersByName(String inputText) throws SQLException {
		Log.w(TAG, inputText);

		return null;
	}
	
	// Извлекает все записи без подстановки
	public Cursor fetchAllOrders() {

		Cursor mCursor = mDb.query(SQLITE_TABLE, new String[] { KEY_ROWID,
				KEY_aNo,
				KEY_displayNo,
				KEY_aCash,
				KEY_aAddress,
				KEY_client,
				KEY_timeB,
				KEY_timeE,
				KEY_tdd,
				KEY_Cont,
				KEY_ContPhone,
				KEY_Packs,
				KEY_Wt,
				KEY_VolWt,
				KEY_Rems,
				KEY_ordStatus,
				KEY_ordType,
				KEY_recType,
				KEY_isready,
				KEY_inway,
				KEY_isview,
				KEY_rcpn,
				KEY_locnumitems}, 
				null, null,	null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Cursor fetchModOrders() {
		
		Cursor mCursor = mDb.rawQuery(SQL_CASE + " order by " + KEY_isview + " asc", null); // 15.05 order by isview для вывода новых записей первыми
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Cursor fetchSortOrders(int sort) {
		Cursor mCursor = null;
		switch (sort) {
		case 0:
			Log.d("OrderDbAdapter", "--- In switch ---");
			mCursor = mDb.rawQuery(SQL_CASE + " order by " + KEY_aAddress + " desc", null);
			break;
		default:
			Log.d("OrderDbAdapter", "--- In switch ---");
			mCursor = mDb.rawQuery(SQL_CASE + " order by " + KEY_aAddress + " asc", null);
			break;
		}
		
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Cursor fetchSortClients(int sort) {
		Cursor mCursor = null;
		switch (sort) {
		case 0:
			Log.d("OrderDbAdapter", "--- In SORT ---");
			mCursor = mDb.rawQuery(SQL_CASE + " order by " + KEY_client + " desc", null);
			break;

		default:
			Log.d("OrderDbAdapter", "--- In SORT ---");
			mCursor = mDb.rawQuery(SQL_CASE + " order by " + KEY_client + " asc", null);
			break;
		}
		
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Cursor fetchSortTimeB(int sort) {
		Cursor mCursor = null;
		switch (sort) {
		case 0:
			Log.d("OrderDbAdapter", "--- In SORT ---");
			mCursor = mDb.rawQuery(SQL_CASE + " order by " + KEY_timeB + " desc", null);
			break;

		default:
			Log.d("OrderDbAdapter", "--- In SORT ---");
			mCursor = mDb.rawQuery(SQL_CASE + " order by " + KEY_timeB + " asc", null);
			break;
		}
		
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	// Взятие/возврат выбранного заказа в работу
	int updOrderCatchIt (long rowid, String aNo) // long rowid, boolean isCatch
	{
		ContentValues cv = new ContentValues();
		int CATCH_OK = 1; // заказ взят
		int CATCH_RES = 0; // заказ сброшен
		int CATCHED_OTHER = 2; // другая запись со статусом взята
		int res = 3;
/*		if (isCatch) {
			cv.put(KEY_inway, "1");
			Log.d(TAG, "Заказ будет взят");
		} else {
			cv.put(KEY_inway, "0");
			Log.d(TAG, "Заказ будет сброшен");
		}*/
		Cursor mCursor = mDb.rawQuery("select aNo from orders where inway = ?", new String[] {"1"});
		if (mCursor != null) {
			if (! mCursor.moveToFirst()) { // не найдены другие записи у которых ЕДУ = 1
				cv.put(KEY_inway, "1");
				Log.d(TAG, "Заказ будет взят");
				res = CATCH_OK;
				mDb.update(SQLITE_TABLE, cv, KEY_ROWID+"=?", new String [] {Long.toString(rowid)});
				Log.d(TAG, "Updated record rowid = " + rowid + " and res = " + res);
			} else { // найдена запись у которой ЕДУ = 1
				String selaNo = mCursor.getString(mCursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_aNo));
				
				if (selaNo.equals(aNo)) { // если она текущая сбрасываем ЕДУ = 0 или возвращаем CATCHED_OTHER если не текущая
					cv.put(KEY_inway, "0");
					Log.d(TAG, "Заказ будет сброшен");
					res = CATCH_RES;
					mDb.update(SQLITE_TABLE, cv, KEY_ROWID+"=?", new String [] {Long.toString(rowid)});
					Log.d(TAG, "Updated record rowid = " + rowid + " and res = " + res);
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
		int rowsUpd = mDb.update(SQLITE_TABLE, cv, KEY_ROWID+"=?", new String [] {Long.toString(rowid)});
		return rowsUpd;
	}
	
	int updLocNumItems (long rowid, String numItems) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_locnumitems, numItems);
		
		int rowsUpd = mDb.update(SQLITE_TABLE, cv, KEY_ROWID+"=?", new String [] {Long.toString(rowid)});
		return rowsUpd;
	}
	
	// Обновление поля заказ просмотрен (при нажатии на кнопку Подробно)
	int updOrderIsView(long rowid) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_isview, "1");
		int rowsUpd = mDb.update(SQLITE_TABLE, cv, KEY_ROWID+"=?", new String [] {Long.toString(rowid)});
		return rowsUpd;
	}
	
	// Необходимо для определения есть ли такой заказ aNo локально или это новая запись которую надо сохранить локально
	boolean isNewOrder(String aNo) {
		boolean res = true; // запись новая локально нет
		String SQL = "select aNo from orders where aNo = ?";
		Cursor mCursor = mDb.rawQuery(SQL, new String[] {aNo});

		if (mCursor.moveToFirst()) {
			res = false; // такая запись есть локально
		}
		Log.d("ORDERDBADAPTER", "isNewOrder record present is " + res);
		return res;
	}
	
	// Удаление несуществующих на сервере записей
	boolean deleteNotExistOrd(String aNoListOnServer) {
		boolean cntDel = false;
		String SQLDEL = "delete from orders where aNo not in (" + aNoListOnServer + ")";
		Cursor mCursor = mDb.rawQuery(SQLDEL, null);
		if (mCursor.moveToFirst()) {
			cntDel = true; 
			Log.d("ORDERDBADAPTER", "record deleted");
		}
		Log.d("ORDERDBADAPTER", "record deleted " + mCursor.getCount());
		return cntDel;
	}
	
	int getCountOrd () {
		String SQLCNT = "select _id from orders";
		Cursor mCursor = mDb.rawQuery(SQLCNT, null);
		
		return mCursor.getCount();
	}

	// Тестовые данные
	public void insertTestEntries () {
		createOrder(
	   //aNo, displayNo, aCash, aAddress, client, timeB, timeE, tdd, Cont, ContPhone, Packs, Wt, VolWt, Rems, ordStatus, ordType, recType, isready, inway, isview, rcpn)
		"1509-9545","1509-9545","NULL","4-Й ЛИХАЧЁВСКИЙ ПЕР. 4","ЭСАБ (4-Й ЛИХ)","NULL","NULL","10:15","МОМОТ","89857274131","2","2.7","0","Документы","NULL","NULL","1","0","0","0","NULL");
		createOrder("3988","3988","NULL","АДМИРАЛА МАКАРОВА, Д.8","ЮПС ЭС СИ ЭС","NULL","NULL","16:42","ЮПС Эс Си Эс","785-71-50","1","0.1","0","","NULL","NULL","1","0","0","0","NULL");
		createOrder("266121","ЗАКАЗ","NULL","ВОЙКОВСКИЙ 5-Й ПР. 2","БЕЗАНТ","9:00","17:30","NULL","любое","720-66-38,720-66-39","NULL","NULL","0","Куда: ROV -ЭКСПРЕСС ГРУЗ   , Получатель: ООО ТТЦ Импульс ПЛЮС +, Адрес:РОСТОВ-на ДОНУ  Семашко 117а/146 оф.12, Контакт: Дьяченко Михаил, Телефон: 2462-586, Примечание отправителя: ЭКЛЗ, Примечание получателя: сч № 2555","NULL","0","0","0","0","0","NULL");
		createOrder("37965","37965","NULL","ВРУБЕЛЯ Д.12 БЦ СОКОЛ 1","МАРАТЕКС","NULL","NULL","12:25","Маратекс","89169904999","1","0.2","0","","NULL","NULL","1","0","0","0","NULL");
		createOrder("1616-3520","1616-3520","NULL","ЛЕНИНГРАДСКИЙ ПР-Т  Д.63 ОФ.504","АПРИОРИ ООО","NULL","NULL","12:05","РЕМИЗОВА ВЛАДА ","9067906193","1","18.6","0","Документы","NULL","NULL","1","0","1","0","NULL");
		createOrder("1619-6322","1619-6322","NULL","ЛЕНИНГРАДСКОЕ ШОССЕ 65 СТР5","СИНЕЛАБЛОГИСТИКА","NULL","NULL","16:30","Синелаблогистика","7893724","2","6","0","","NULL","NULL","1","0","0","0","NULL");
		createOrder("32002431","32002431","NULL","ЛЕНИНГРАДСКОЕ ШОССЕ Д. 71 Г","ООО МЕТРО КЕШ ЭНД КЕРРИ","NULL","NULL","16:10","ООО Метро Кеш Энд Ке","8-495-502-17-72","1","0.2","0","","NULL","NULL","1","0","0","0","NULL");
		createOrder("1188354","1188354","NULL","ПР-Д.СТАРОПЕТРОВСКИЙ  Д.7А,  КОРП.25  АП.4", "ООО БЕЛЛЬ","NULL","NULL","12:52","ООО Белль","Сероштанова Светлана 495-","1","0.1","0","","NULL","NULL","1","0","0","0","NULL");
		createOrder("812006317","812006317","NULL","СМОЛЬНАЯ 31 КВ 79","ВЕКТОРФОИЛТЕК","NULL","NULL","NULL","ТАТЬЯНА","9162391516","1","0.1","0","Документы","NULL","NULL","1","0","0","0","NULL");
		createOrder("266130","ЗАКАЗ","NULL","СМОЛЬНАЯ УЛ. 14","СПЕКТР-АВИА ООО","13:30","18:00","NULL","СВЕТЛАНА .......","2312830","NULL","NULL","NULL","NULL","NULL","1","0","0","0","0","NULL");
		createOrder("1286-6456","1286-6456","NULL","СМОЛЬНАЯ УЛ. 20А","ГЕКСАГОН ЦШК ООО","NULL","NULL","15:15","ЗЕЛЕНЧУК ВЛАДИМИР","89260012941","1","1.08","0","деталь с возвратом","NULL","NULL","1","0","0","0","NULL");
		createOrder("266200","ЗАКАЗ","NULL","СМОЛЬНАЯ УЛ. 20А","ГЕКСОГОН","NULL","NULL","NULL","Антон ..Наталья","7887920","NULL","NULL","NULL","NULL","NULL","0","0","0","0","0","NULL");
		createOrder("1203-7452","1203-7452","NULL","СТАРОПЕТРОВСКИЙ ПР-Д Д. 7 А СТР. 25","БЕЛЛЬ ООО ","NULL","NULL","12:52","ЗВОНКОВА","495-641-5790","1","0.7","0","Документы","NULL","NULL","1","0","0","0","NULL");
		createOrder("1366-5298","1366-5298","NULL","УЛ ПРАВОБЕРЕЖНАЯ СТР 1Б","МЕДИА МАРКТ","NULL","NULL","15:58","ЖИЛЯБИНА ИРИНА","89250820980","1","1.26","0","ПЛАСТИКОВАЯ КАРТА","NULL","NULL","1","0","0","0","NULL");
	}
}
