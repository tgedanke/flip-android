package com.informixonline.courierproto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ActDlvDetail  extends Activity {
	
	TextView tvDorder_num, tvDorder_state, tvDacash, tvDaddr, tvDcomp_name, tvDcontact, tvDcontact_num, tvDtimeBE, 
	tvDpos_num, tvDweight, tvDvol_weight, tvDcomment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actdlvdetail);
		
		tvDorder_num = (TextView)findViewById(R.id.tvDorder_numDlv);

		tvDaddr = (TextView)findViewById(R.id.tvDaddrDlv);		
		tvDcomp_name = (TextView)findViewById(R.id.tvDcomp_nameDlv);
		tvDcontact = (TextView)findViewById(R.id.tvDcontactDlv);
		tvDcontact_num = (TextView)findViewById(R.id.tvDcontact_numDlv);
		tvDpos_num = (TextView)findViewById(R.id.tvDpos_numDlv);
		tvDweight = (TextView)findViewById(R.id.tvDweightDlv);
		tvDvol_weight = (TextView)findViewById(R.id.tvDvol_weightDlv);
		tvDcomment = (TextView)findViewById(R.id.tvDcommentDlv);		
		
		Intent intent = getIntent();
		
		String Dorder_num = intent.getStringExtra("tvDorder_num");
		tvDorder_num.setText(Dorder_num);
		
/*		String tvDorder_state_ordStatus = intent.getStringExtra("tvDorder_state_ordStatus");
		tvDorder_state.setText(tvDorder_state_ordStatus);*/
		
		//tvDacash.setText(intent.getStringExtra("tvDacash"));
		tvDaddr.setText(intent.getStringExtra("tvDaddr_aAddress"));
		tvDcomp_name.setText(intent.getStringExtra("tvDcomp_name_client"));
		tvDcontact.setText(intent.getStringExtra("tvDcontact_Cont"));
		tvDcontact_num.setText(intent.getStringExtra("tvDcontact_num_ContPhone"));
		//tvDtimeBE.setText(intent.getStringExtra("tvDtimeBE"));
		tvDpos_num.setText(intent.getStringExtra("tvDpos_num_Packs"));
		tvDweight.setText(intent.getStringExtra("tvDweight_Wt"));
		tvDvol_weight.setText(intent.getStringExtra("tvDvol_weight_VolWt"));
		tvDcomment.setText(intent.getStringExtra("tvDcomment"));
	}

}
