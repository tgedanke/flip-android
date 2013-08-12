package com.flippost.courier;

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
		
		//String tvDorder_state_ordStatus = intent.getStringExtra("tvDorder_state_ordStatus");
		//tvDorder_state.setText(tvDorder_state_ordStatus);
		
		String strtvDorder_state_ordStatus = intent.getStringExtra("tvDorder_state_ordStatus");
		if (strtvDorder_state_ordStatus.equalsIgnoreCase("null")) {strtvDorder_state_ordStatus = "";};
		tvDorder_state.setText(strtvDorder_state_ordStatus);
		
		String strtvDacash = intent.getStringExtra("tvDacash");
		if (strtvDacash.equalsIgnoreCase("null")) {strtvDacash = "0";};
		tvDacash.setText(strtvDacash);
		
		String strtvDaddr_aAddress = intent.getStringExtra("tvDaddr_aAddress");
		if (strtvDaddr_aAddress.equalsIgnoreCase("null")) {strtvDaddr_aAddress = "";};
		tvDaddr.setText(strtvDaddr_aAddress);
		
		String strtvDcomp_name_client = intent.getStringExtra("tvDcomp_name_client");
		if (strtvDcomp_name_client.equalsIgnoreCase("null")) {strtvDcomp_name_client = "";};
		tvDcomp_name.setText(strtvDcomp_name_client);
		
		String strtvDcontact_Cont = intent.getStringExtra("tvDcontact_Cont");
		if (strtvDcontact_Cont.equalsIgnoreCase("null")) {strtvDcontact_Cont = "";};
		tvDcontact.setText(strtvDcontact_Cont);
		
		String strtvDcontact_num_ContPhone = intent.getStringExtra("tvDcontact_num_ContPhone");
		if (strtvDcontact_num_ContPhone.equalsIgnoreCase("null")) {strtvDcontact_num_ContPhone = "";};
		tvDcontact_num.setText(strtvDcontact_num_ContPhone);
		
		String strtvDtimeBE = intent.getStringExtra("tvDtimeBE");
		if (strtvDtimeBE.indexOf("null")>=0) {strtvDtimeBE = "";};
		tvDtimeBE.setText(strtvDtimeBE);
		
		String strtvDpos_num_Packs = intent.getStringExtra("tvDpos_num_Packs");
		if (strtvDpos_num_Packs.equalsIgnoreCase("null")) {strtvDpos_num_Packs = "";};
		tvDpos_num.setText(strtvDpos_num_Packs);
		
		String strtvDweight_Wt = intent.getStringExtra("tvDweight_Wt");
		if (strtvDweight_Wt.equalsIgnoreCase("null")) {strtvDweight_Wt = "";};
		tvDweight.setText(strtvDweight_Wt);
		
		String strtvDvol_weight_VolWt = intent.getStringExtra("tvDvol_weight_VolWt");
		if (strtvDvol_weight_VolWt.equalsIgnoreCase("null")) {strtvDvol_weight_VolWt = "";};
		tvDvol_weight.setText(strtvDvol_weight_VolWt);
		
		String strtvDcomment = intent.getStringExtra("tvDcomment");
		if (strtvDcomment.equalsIgnoreCase("null")) {strtvDcomment = "";}
		tvDcomment.setText(strtvDcomment);

	}
}