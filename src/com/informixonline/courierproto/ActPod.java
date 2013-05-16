package com.informixonline.courierproto;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActPod extends Activity implements OnClickListener {
	
	TextView tvPodOrdNum;
	EditText edtPodTime, edtPodDest;
	Button btnOk, btnCancel;
	Date c = Calendar.getInstance().getTime(); //TimeZone.getTimeZone("Europe/Moscow")
	TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
	
	@Override
	protected void onCreate(Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);
		setContentView(R.layout.actpod);
		
		btnOk = (Button)findViewById(R.id.btnPodOk);
		btnOk.setOnClickListener(this);
		btnCancel = (Button)findViewById(R.id.btnPodCancel);
		btnCancel.setOnClickListener(this);
		
		Intent intent = getIntent();
		
		String aNo = intent.getStringExtra("tvDorder_num");
		tvPodOrdNum = (TextView)findViewById(R.id.tvPodOrdNum);
		tvPodOrdNum.setText(aNo);
		
		edtPodTime = (EditText)findViewById(R.id.edtPodTime);
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		timeFormat.setTimeZone(tz);
		//edtPodTime.setText(new SimpleDateFormat("HH:mm").format(c)); //"HHmmss"
		edtPodTime.setText(timeFormat.format(c));
		
		edtPodDest = (EditText)findViewById(R.id.edtPodDest);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnPodOk:
			Intent intentOk = new Intent();
			intentOk.putExtra("wb_no", tvPodOrdNum.getText().toString());
			
			//String date = new SimpleDateFormat("yyyyMMdd").format(c);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			dateFormat.setTimeZone(tz);
			intentOk.putExtra("p_d_in", dateFormat.format(c));
			
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
			timeFormat.setTimeZone(tz);
			//String time = new SimpleDateFormat("HH:mm").format(c);
			intentOk.putExtra("tdd", timeFormat.format(c));
			intentOk.putExtra("rcpn", edtPodDest.getText().toString());
			
			setResult(RESULT_OK, intentOk);
			finish();
			break;
		
		case R.id.btnPodCancel:
			setResult(RESULT_CANCELED);
			finish();
			break;

		default:
			break;
		}
		
	}
}
