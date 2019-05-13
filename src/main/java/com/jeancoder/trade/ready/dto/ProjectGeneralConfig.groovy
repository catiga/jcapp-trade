package com.jeancoder.trade.ready.dto

import java.sql.Timestamp

import com.jeancoder.jdbc.bean.JCBean
import com.jeancoder.jdbc.bean.JCForeign
import com.jeancoder.jdbc.bean.JCID

class ProjectGeneralConfig {
	
	BigInteger id;

	BigInteger proj_id;
	
	String disname;
	
	String sc_code;
	
	String sc_name;
	
	String sc_info;
	
	String sc_type;
	
	String sc_jt;
	
	Timestamp c_time;
	
	Integer flag;
	
	String partner;
	
	String mchid;
	
	String pri_key;
	
	Integer pri_key_type;
	
	String pri_k_p;
	
	String pub_key;
	
	Integer pub_key_type;
	
	String pub_k_p;
	
	Integer rollback;
	
	String pri_key_format;
	
	String pub_key_format;
	
	String acc_fld;
	
	Integer rb_key_type;
	
	String rb_key_format;
	
	String rb_key;
	
	String rb_kp;
	
	String rb_file;
	
	BigInteger paro;

}
