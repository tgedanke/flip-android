package com.informixonline.courierproto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ActNumItems extends Activity implements OnClickListener {
	
	EditText edtNumItems;
	Button btnOk, btnCancel;
	long ordersId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actnumitem);
		
		edtNumItems = (EditText)findViewById(R.id.edtNumItems);
		btnOk = (Button)findViewById(R.id.btnOkNumItems);
		btnOk.setOnClickListener(this);
		btnCancel = (Button)findViewById(R.id.btnCancNumItems);
		btnCancel.setOnClickListener(this);
		
		Intent intent = getIntent();
		String numItems = intent.getStringExtra("tvLocNumItems");
		ordersId = intent.getLongExtra("ordersid", 0);
		edtNumItems.setText(numItems);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOkNumItems:
			Intent intentOk = new Intent();
			intentOk.putExtra("numitems", edtNumItems.getText().toString());
			intentOk.putExtra("ordersid", this.ordersId);
			setResult(RESULT_OK, intentOk);
			finish();
			break;

		case R.id.btnCancNumItems:
			setResult(RESULT_CANCELED);
			finish();
			break;
			
		default:
			finish();
			break;
		}
		
	}
}
