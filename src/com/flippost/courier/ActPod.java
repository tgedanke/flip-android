package com.flippost.courier;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ActPod extends Activity implements OnClickListener {

	TimePicker tp;
	TextView tvPodOrdNum;
	EditText edtPodDest;
	Button btnOk, btnCancel;
	long ordersId;

	@Override
	protected void onCreate(Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);
		setContentView(R.layout.actpod);

		Calendar CL = Calendar.getInstance();

		tp = (TimePicker) findViewById(R.id.timePicker1);
		tp.setIs24HourView(true);
		tp.setCurrentHour(CL.get(Calendar.HOUR_OF_DAY));

		btnOk = (Button) findViewById(R.id.btnPodOk);
		btnOk.setOnClickListener(this);
		btnCancel = (Button) findViewById(R.id.btnPodCancel);
		btnCancel.setOnClickListener(this);

		Intent intent = getIntent();

		ordersId = intent.getLongExtra("ordersid", 0);

		String aNo = intent.getStringExtra("tvDorder_num");
		tvPodOrdNum = (TextView) findViewById(R.id.tvPodOrdNum);
		tvPodOrdNum.setText(aNo);

		edtPodDest = (EditText) findViewById(R.id.edtPodDest);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnPodOk:
			Intent intentOk = new Intent();
			intentOk.putExtra("ordersid", this.ordersId);
			intentOk.putExtra("wb_no", tvPodOrdNum.getText().toString());

			Calendar CL = Calendar.getInstance();

			// String date = new SimpleDateFormat("yyyyMMdd").format(c);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			intentOk.putExtra("p_d_in", dateFormat.format(CL.getTime()));

			CL.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour());
			CL.set(Calendar.MINUTE, tp.getCurrentMinute());
			SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
			String tdd = tf.format(CL.getTime());
			Log.d("ACT_POD", tdd);
			intentOk.putExtra("tdd", tdd);

			String rcpn = edtPodDest.getText().toString().trim();

			if (rcpn.isEmpty()) {
				Toast.makeText(getApplicationContext(), "Укажите фамилию получателя", Toast.LENGTH_LONG).show();
			} else {
				intentOk.putExtra("rcpn", edtPodDest.getText().toString());

				setResult(RESULT_OK, intentOk);
				finish();
			}
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
