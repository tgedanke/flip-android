package com.informixonline.courierproto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ActOrderDetail extends Activity {
	
	TextView tvDorder_num, tvDorder_state, tvDacash, tvDaddr, tvDcomp_name, tvDcontact, tvDcontact_num, tvDtimeBE, 
	tvDpos_num, tvDweight, tvDvol_weight, tvDcomment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actorderdetail);
		
		tvDorder_num = (TextView)findViewById(R.id.tvDorder_num);
		tvDorder_state = (TextView)findViewById(R.id.tvDorder_state);
		tvDacash = (TextView)findViewById(R.id.tvDacash);
		tvDaddr = (TextView)findViewById(R.id.tvDaddr);		
		tvDcomp_name = (TextView)findViewById(R.id.tvDcomp_name);
		tvDcontact = (TextView)findViewById(R.id.tvDcontact);
		tvDcontact_num = (TextView)findViewById(R.id.tvDcontact_num);
		tvDtimeBE = (TextView)findViewById(R.id.tvDtimeBE);
		tvDpos_num = (TextView)findViewById(R.id.tvDpos_num);
		tvDweight = (TextView)findViewById(R.id.tvDweight);
		tvDvol_weight = (TextView)findViewById(R.id.tvDvol_weight);
		tvDcomment = (TextView)findViewById(R.id.tvDcomment);		
		
		Intent intent = getIntent();
		
		String Dorder_num = intent.getStringExtra("tvDorder_num");
		tvDorder_num.setText(Dorder_num);
		
		String tvDorder_state_ordStatus = intent.getStringExtra("tvDorder_state_ordStatus");
		tvDorder_state.setText(tvDorder_state_ordStatus);
		
		tvDacash.setText(intent.getStringExtra("tvDacash"));
		tvDaddr.setText(intent.getStringExtra("tvDaddr_aAddress"));
		tvDcomp_name.setText(intent.getStringExtra("tvDcomp_name_client"));
		tvDcontact.setText(intent.getStringExtra("tvDcontact_Cont"));
		tvDcontact_num.setText(intent.getStringExtra("tvDcontact_num_ContPhone"));
		tvDtimeBE.setText(intent.getStringExtra("tvDtimeBE"));
		tvDpos_num.setText(intent.getStringExtra("tvDpos_num_Packs"));
		tvDweight.setText(intent.getStringExtra("tvDweight_Wt"));
		tvDvol_weight.setText(intent.getStringExtra("tvDvol_weight_VolWt"));
		tvDcomment.setText(intent.getStringExtra("tvDcomment"));

	}
}