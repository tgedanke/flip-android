package com.informixonline.courierproto;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/*
 * Сохранение и чтение настроек приложения
 */
public class ActSettings extends Activity implements OnClickListener {
	
	// Save setting to SharedPref
	//final String APPCFG_LOGIN_URL = "LOGIN_URL";
	//final String APPCFG_GETDATA_URL = "GETDATA_URL";
	SharedPreferences sharedAppConfig;
	
	EditText edtLOGIN_URL, edtGETDATA_URL;
	Button btnSaveSet;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actsettings);
		Log.d("ActSettings", "--- OnCreate ActSettings ---");
		
		edtLOGIN_URL = (EditText)findViewById(R.id.edtLOGIN_URL);
		edtGETDATA_URL = (EditText)findViewById(R.id.edtGETDATA_URL);
		btnSaveSet = (Button)findViewById(R.id.btnSaveSet);
		
		//sharedAppConfig = getPreferences(MODE_PRIVATE);
		sharedAppConfig = getSharedPreferences(CourierMain.SHAREDPREF, MODE_PRIVATE);
		
		// Read prev settings
		edtLOGIN_URL.setText(sharedAppConfig.getString(CourierMain.APPCFG_LOGIN_URL, ""));
		edtGETDATA_URL.setText(sharedAppConfig.getString(CourierMain.APPCFG_GETDATA_URL, ""));
		
		btnSaveSet.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSaveSet:
			
			Editor ed = sharedAppConfig.edit();
			ed.putString(CourierMain.APPCFG_LOGIN_URL, edtLOGIN_URL.getText().toString());
			ed.putString(CourierMain.APPCFG_GETDATA_URL, edtGETDATA_URL.getText().toString());
			ed.commit();
			break;

		default:
			break;
		}
		
	}
}
