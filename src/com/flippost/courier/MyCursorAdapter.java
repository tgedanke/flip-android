package com.flippost.courier;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/*
 * Адаптер для вывода элементов списка
 */

//@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) 
@TargetApi(16)
public class MyCursorAdapter extends SimpleCursorAdapter {
	   
	   public MyCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		inflater = LayoutInflater.from(context);
	}
	LayoutInflater inflater;

	   
	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		
		LinearLayout llvMain = (LinearLayout)view.findViewById(R.id.llvMain);
		TextView tvRecType = (TextView)view.findViewById(R.id.tvRecType);
		tvRecType.setText(cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_recType)));
		
		TextView tvOSorDNorEmp = (TextView)view.findViewById(R.id.tvOSorDNorEmp);
		String strOSorDNorEmp = cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_OSorDNorEMP));
		if (! strOSorDNorEmp.equalsIgnoreCase("null")) {
			tvOSorDNorEmp.setText(strOSorDNorEmp);
		} else {
			tvOSorDNorEmp.setText("");
		}
		
		TextView tvCh = (TextView)view.findViewById(R.id.tvCh);
		String strTvCh = cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_aCash));
		if (! (strTvCh.equalsIgnoreCase("null"))) {
			tvCh.setTextColor(Color.YELLOW);
			tvCh.setText("$");
		} else {
			tvCh.setText(" ");
		}
		
		TextView inWay = (TextView)view.findViewById(R.id.tvInWay);		
		if (cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_inway)).equals("Еду")) {
			// llvMain.setBackgroundColor(Color.rgb(255,228,181));
			inWay.setTextColor(Color.BLUE);
			inWay.setText(cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_inway)));
		} else {
			// llvMain.setBackgroundColor(Color.WHITE);
			inWay.setTextColor(Color.GRAY);
			inWay.setText("Еду");			
		}
		//inWay.setText(cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_inway)));
		
		// Подсветка выбранной записи и остальных
		if (CourierMain.ordersId == cursor.getLong(cursor.getColumnIndexOrThrow(OrderDbAdapter.KEY_ROWID))) {
			// выбранная запись - зеленым
			// llvMain.setBackgroundColor(Color.rgb(0,255,127)); теперь за подсветку выбранного отвечает селектор
		} else if (cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_isview)).equals("1")) {
			// просмотренные записи - белым
			llvMain.setBackgroundColor(Color.WHITE);
		} else {
			// остальные голубым
			llvMain.setBackgroundColor(Color.rgb(135,206,250));
		}
		

		
		TextView tvAddress = (TextView)view.findViewById(R.id.tvaAddress);
		String strtvAddress = cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_aAddress));
		if (strtvAddress.equalsIgnoreCase("null")) {
			tvAddress.setText("");
		} else {
			tvAddress.setText(strtvAddress);
		}
		
		TextView tvClient = (TextView)view.findViewById(R.id.tvClient);
		String strtvClient = cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_client));
		if (strtvClient.equalsIgnoreCase("null") || strtvClient.equals(0)) {
			tvClient.setText("");
		} else {
			tvClient.setText(strtvClient);
		}
		
		TextView tvTimeBE = (TextView)view.findViewById(R.id.tvTimeBE);
		String timeBE = cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_timeBE));
		if(timeBE.indexOf("null")>=0) timeBE = "";
		tvTimeBE.setText(timeBE);
		
		TextView tvNumt = (TextView)view.findViewById(R.id.tvNumt);
		TextView tvNum = (TextView)view.findViewById(R.id.tvNum);
		
		// Тип 0 - заказ, 1 - накладная, 2 - счет
		TextView tvIsredy = (TextView)view.findViewById(R.id.tvIsredy);
		String recType_forDetail = cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_recType_forDetail));
		tvNumt.setText("");
		tvNum.setText("");
		if (recType_forDetail.equals("0")) {
			// Только для заказов
			// Установка цвета ОК 
			//cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_recType_forDetail))
			if (cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_isready)).equals("1")) {
				tvIsredy.setTextColor(Color.BLUE);
				//tvIsredy.setText(cursor.getColumnIndex(OrderDbAdapter.KEY_isready));
				tvIsredy.setText("Ок");
			} else {
				tvIsredy.setTextColor(Color.GRAY);
				tvIsredy.setText("Ок");
			}
			//tvIsredy.setText(cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_isready)));
			
			tvNumt.setText(" Кол-во:");
			tvNum.setText(cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_locnumitems)));
			//Log.d("ADAPTER", "Кол-во" + cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_locnumitems)));
			
		} else if (recType_forDetail.equals("1")) {
			// накладные
			if (cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_tdd)).equalsIgnoreCase("null")) {
				tvIsredy.setText("ПОД");
				tvIsredy.setTextColor(Color.BLUE);
				//tvIsredy.setText(cursor.getColumnIndex(OrderDbAdapter.KEY_isready));
			} else {
				tvIsredy.setText(cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_tdd)));
				tvIsredy.setTextColor(Color.GRAY);
			}
			
		} else if (recType_forDetail.equals("2")) {
			// счет
			// Установка цвета ОК 
			//cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_recType_forDetail))
			if (cursor.getString(cursor.getColumnIndex(OrderDbAdapter.KEY_isready)).equals("1")) {
				tvIsredy.setTextColor(Color.BLUE);
				//tvIsredy.setText(cursor.getColumnIndex(OrderDbAdapter.KEY_isready));
				tvIsredy.setText("Ок");
			} else {
				tvIsredy.setTextColor(Color.GRAY);
				tvIsredy.setText("Ок");
			}
		}
		
	}
	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup parent) {
		return inflater.inflate(R.layout.orders_info, parent, false);
	}

}
