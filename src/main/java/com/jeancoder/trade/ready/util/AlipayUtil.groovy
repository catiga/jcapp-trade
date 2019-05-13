package com.jeancoder.trade.ready.util

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec

import com.jeancoder.app.sdk.source.LoggerSource
import com.jeancoder.core.log.JCLogger
import com.jeancoder.core.util.JackSonBeanMapper

class AlipayUtil {
	private static JCLogger logger = LoggerSource.getLogger(AlipayUtil.class);
	def head;
	def body;

	def sign() {
		if(!head||!body)
			return null;
		def json = JackSonBeanMapper.toJson(body);
		
		head['biz_content'] = json;
		head = head.sort{it.key}
		
		println head;

		def buff = new StringBuffer();
		head.each{
			a,b ->
			buff.append(a+'='+b + '&');
		}
		def content = buff.substring(0, buff.length() - 1);
		def charset = 'UTF-8';

		def encode = encode(charset, content);
		
		return encode;
	}
	
	def request() {
		head['sign'] = this.sign();
		def buff = new StringBuffer();
		head.each({
			a,b ->
			def tmp_b = URLEncoder.encode(b, 'UTF-8');
			buff.append(a+'='+tmp_b + '&');
		})
		def request_url = 'https://openapi.alipay.com/gateway.do?' + buff.substring(0, buff.length() - 1);
		return request_url;
	}
	
	def encode(def charset, String content) {
		try {
			PrivateKey priKey = getPrivateKeyFromPKCS8(new ByteArrayInputStream(app_private_key.getBytes()));

			java.security.Signature signature = java.security.Signature.getInstance('SHA256WithRSA');

			signature.initSign(priKey);

			if(!charset) {
				signature.update(content.getBytes());
			} else {
				signature.update(content.getBytes(charset));
			}

			byte[] signed = signature.sign();

			return new String(Base64.getEncoder().encode(signed));
		} catch (Exception e) {
			logger.error("AlipayUtil _________",e)
			throw new Exception("RSAcontent = " + content + "; charset = " + charset, e);
		}
	}

	public static PrivateKey getPrivateKeyFromPKCS8(InputStream ins) throws Exception {
		if (ins == null) {
			return null;
		}

		KeyFactory keyFactory = KeyFactory.getInstance('RSA');

		byte[] encodedKey = StreamUtil.readText(ins).getBytes();

		encodedKey = Base64.getDecoder().decode(encodedKey);

		return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
	}

	/*
	static def app_id = '2018080560940196';
	static def seller_id = '2088812742462668';
	static def alipay_pub_key = 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAotYiBMpcxUUXHH6DjGc9rRzdiVtDAcN65aVw3zEeu15WaEjf8hdFojV95LPaSJAMBEXCKDyb5hBV8y1EQu68Gq76laVhw3FmQxd9xx5Y4DEw93Pm4uvMnhoMvs/rY1znkhzjhmtoBybb0foGvp98UC9uWybMnJXJIgANf+YDm15VvEZ6xqCBZy/XOOTX6WMf1BjU9F8dId1ZuBASzm5Fxe8uDgUehOZK8hBd7SVKui1RXCvpeLfAL5FXXQRQiBvhN0CFaTT6YmPq8tfSKp/21rR/CEOK4CWcxKZ4JWawfZkWReRu1ffzZ0x1pWtKFta8Ilykc7c5Fa4xdGls1RgpWQIDAQAB';
	static def app_private_key = 'MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCNCmAvXGJVWx/CR9C2KyckC5eCDYvUIeY0bvIkwYQbHBWSKZekrEGsxJk/JF3KyJozhqvuq4uC8jlRnI7OfHeNQIK1WRn1IBU+dnvGYuDb/Z0R7TKuDGwzxnJHt38TkHp5vlteGPyNWPRCNMSOcKZsA8cmOcuVeTF30NEIwytqqa5kPZN1tSES13cg4OYkjpOrhCNNff2fmN/pdqEPCBVoLBbLvp6tT74PK9PQb+TO6LZ3+ItiSn7Xz2lE+u60Ow/lC/NKABIBaIDnYWS2ub8XxZefAoHKmsxhn0IeQ1hhAHB4B9IdWH+e99Q9mBfdM6A6Rj5vZy3tSMoXLfhOjPNxAgMBAAECggEAbrqCoSwql1CfMmwnU521cPsYy6XO7ZBfpzKjMHAtQwtddA7rWwFR3k3K8mOeryGmzmUgfpR3RyrcDVKYW0wkn+TRuYZmdqflhdCHhE0EtDH8KqLKcxKVsGBQQDWzsqxvOz1ThiCfxmjb+05qMloGji65Hy2ow4UObT6nnfV8FJRPLBS5Q7M53fJLEpIwIqkL+eJXTmaxIMeatETgXYgRR+vMdh5BKYsrUr5hy42Cm8ZmmvMqBQCasBslm+RoN8AsTsNAW/k/sV2dyyJSkNpnpwTXRX9ml0EjpkEjcHYXwT7dKTeHQqxTRM4+fkw+trmIuoS3eYuaosbCyG778VuiQQKBgQDPhalAh7Ne+ELlOaDQzeV8wBLsjki3Ztj+usQQi+pvulIlECOpJcduIDpLwCQRqFamz+sjANJnQRJ+Atoio/xBL7qUjKAsGP8+/PXp3Zvdp20q0X1sMLGrnqsvDvx5RT1dIuULDUxiZUBGhK7GVK8rpWHjeMTApi6Qe21vXlQcjQKBgQCt/PNPSRiZFHMhIlA138ebLJHyYTNn5ghx7jykSGcSXT4hFxTc2hWh4WbPwtdEhrZZw9OqxevscAoM/z9JWJZbM7ziomtS0ZjOwjyuhJaBIDKKPwcft9g8kVVtRIbvZECLwmofwVEf8kfySN6Bs4tdN2KQbdAAa+8mNTpVY+9DdQKBgQCUh8tDV50nnTNsE5opuSTG7EbfL1uWgTNQ5uZMHYi3XTlMM8gCWfrqtIBlFWn7hWw3yts2W3E83BNbQ7lTT+HN2laF2j0OMvoF4FI2iBn2RM5a9kP5+sQ/3LoTXXuRZOBt1/rttR/10Rh3IKJja9tQtvdgM2FHrenoGZ+TNYWoRQKBgAwvgSL2baCbDA0wCAod6C74Cii9ogoO1FyWYOzonT9uBGHw6ecl5wn1bWvzq3wDK1ZSHssBHarrleifH+GVMMKmdv4wfe/I+jjQ5csbQic9CA0iqO8RwzVKwH7so760eMjRDvm2YmYoY92WOO9AkewvatnOtxiwdolohPNiEmnZAoGAUoCHpp4VhpM1zTxNs3bLpdM/JPECxz96DUV4v8Ziuh09ehBS/DxRWVCn7OisKI7bdMnJh1t7q9SAqZDELvPUJYN9gym+90A1JdHqxwoKs1k1qGWpyp2LobRJEZytM+xfN7GdOlD3bmqRnt4KSmc4+XSlcrWTaNzHun7EsjfeBoA=';
	*/
	def app_id; def seller_id; def alipay_pub_key; def app_private_key;

	static void main(def argc) {
//		AlipayUtil au = new AlipayUtil();
//		def head = [:];
//		head['app_id'] = au.app_id;
//		head['method'] = 'alipay.trade.pay';
//		head['format'] = 'JSON';
//		head['charset'] = 'utf-8';
//		head['sign_type'] = 'RSA2';
//		head['timestamp'] = DateUtil.format(Calendar.getInstance().getTime(), 'yyyy-MM-dd HH:mm:ss');
//		head['version'] = '1.0';
//
//		def body = [:];
//		body['out_trade_no'] = '1324124124';
//		body['scene'] = 'bar_code';
//		body['auth_code'] = '289179686864713886';
//		body['subject'] = '扫码';
//		body['seller_id'] = '2088812742462668';
//		body['total_amount'] = '0.01';
//
//		au.head = head; au.body = body.sort();
//
//		println au.request();
		//def app_private_key  = 'MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCMTdj6hYBW9z3Rsl5vmpcbwzOKby81fJaJ+pGLQ/a6mJyUMeThvIAOozSvw4i7uKayN8PLXU06w9KAoXLnCeUhY8bEnNJ7U45J2ITVDlA9vDfvZs47Ly6l11Pjvwi2m48x7HBf66VcgG3Xy94/g0Yrlp/EpbNozZ64WYjdM6mwxXsoMYCfNUgmba80SxqFf0fiWZj1GS2LwfBLw3w0KodSakYDKIg2QZ0vPD/hZIsiXlAC0J3OGQhEK3R4zs7EK/301H9s+yqkV3GKjK/X1XqgIk3co0fEBOjsejQeS30Z5sVr513l8mhTcL60RBeSGzWpmHk90xnYj1+gffrhW+GPAgMBAAECggEAcRTJXdk9wFoTfDCsBn1tbxXcM6Y7QXvCBUFsDvffIzOfd53jIMGB4MpPy53oVYyU5i0l5IG1njh4L+5wIqCKCWhWqt7OklGc7A3TubiImclat9x/OZpkmYu8dGHjDvK/7ffn9peBttVYMsaAwWX3p2gGK0N2H3EijOi087wQ94RqNsQcZNT03icr5HhFUhikU0UjBTMEO5ptAbB25ELC6EZWcZB+wh7I5j7lvP5RTbzSX/0OTsMzXBodu++sY/VG5osVRLCTZjMAC3JmovPBNKL0CyreMLHBntpW1YEkV8jtIDUMD3ny7kPcs4YASKAbH2ekpXkVr6eP4/GWMW/WYQKBgQDsOE2wxUDYYamRpwzNe6Pt1fV9nDYcgXhAOUHQjO/AUDv5YRlDbGJDOvrAN8n22l+EDA+3DpgmxzXPAny2XKWH9dbBf04vZblijj3V04IElJR+vuQg4ppCdeGLQOOeaGqEpu9pIjAxxyG5kLuoCnADe8NWgaTOTW4XRHjah4JqkQKBgQCYDXUIi/PgN8jNRS8X7F2gCvWFgml/jt34V/XTPIUQiRQoi8o+Erat0Z0YBjYpdnza6psgOCupx8OMW1VEq3dEhLer2hO1aCQKWaBWHbrSp/YVkyonBfDFuHnMW2HmUToBfziPNlTKVoSKb8fKcYhZuXaH1+jHt+f5bQxrdnpaHwKBgQCgvLNI7QBx/Qq4vNY+YLqslSHmJky/2u2rrgp10eE2mKIm0IazVWYL329PKuc4HWJPZTGwW7L2+ClKQqQAJ0zQFl3a7ziRkgOy5512CeV4Hm5pTJXJNsjHHpWp/y6j1ONefoV8obHvvVPahHzdHLUZ+bZy/+bYJuIZ3RLriLgasQKBgC0DvuQa2sZEfZQXqUuB1G3vEKTH6Qe0eg5tq/8vaY1tp6QhTmx2CdMHvWRBYoN/6pK85dPyDSskUsYYu/Oqd5K3X9DmrPMFs7XEtu0HvCllRDs66D3JSnCenqBbZW5UGcIbsdjkEtIcLvz0/sb2OZGmkGrDBs0pquly2TxvROWtAoGBALVJrpi+Dk4CGEIfLLpScamEhDcA5ZUM4oOokuVX4b20wYF7MLZU91/9XkQ5RYHH1iuXJNLrhoVDJGOwUU0aVCD1XyeEqz9AUrOeZxc4oYN5ykuszUgD/LTputr7C2u+nEf8Rz0tQcYVyZLOVq8GwEaR7lPpphHm9Yq/5/XUjWGt'
				def app_private_key  = 'MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCMTdj6hYBW9z3Rsl5vmpcbwzOKby81fJaJ+pGLQ/a6mJyUMeThvIAOozSvw4i7uKayN8PLXU06w9KAoXLnCeUhY8bEnNJ7U45J2ITVDlA9vDfvZs47Ly6l11Pjvwi2m48x7HBf66VcgG3Xy94/g0Yrlp/EpbNozZ64WYjdM6mwxXsoMYCfNUgmba80SxqFf0fiWZj1GS2LwfBLw3w0KodSakYDKIg2QZ0vPD/hZIsiXlAC0J3OGQhEK3R4zs7EK/301H9s+yqkV3GKjK/X1XqgIk3co0fEBOjsejQeS30Z5sVr513l8mhTcL60RBeSGzWpmHk90xnYj1+gffrhW+GPAgMBAAECggEAcRTJXdk9wFoTfDCsBn1tbxXcM6Y7QXvCBUFsDvffIzOfd53jIMGB4MpPy53oVYyU5i0l5IG1njh4L+5wIqCKCWhWqt7OklGc7A3TubiImclat9x/OZpkmYu8dGHjDvK/7ffn9peBttVYMsaAwWX3p2gGK0N2H3EijOi087wQ94RqNsQcZNT03icr5HhFUhikU0UjBTMEO5ptAbB25ELC6EZWcZB+wh7I5j7lvP5RTbzSX/0OTsMzXBodu++sY/VG5osVRLCTZjMAC3JmovPBNKL0CyreMLHBntpW1YEkV8jtIDUMD3ny7kPcs4YASKAbH2ekpXkVr6eP4/GWMW/WYQKBgQDsOE2wxUDYYamRpwzNe6Pt1fV9nDYcgXhAOUHQjO/AUDv5YRlDbGJDOvrAN8n22l+EDA+3DpgmxzXPAny2XKWH9dbBf04vZblijj3V04IElJR+vuQg4ppCdeGLQOOeaGqEpu9pIjAxxyG5kLuoCnADe8NWgaTOTW4XRHjah4JqkQKBgQCYDXUIi/PgN8jNRS8X7F2gCvWFgml/jt34V/XTPIUQiRQoi8o+Erat0Z0YBjYpdnza6psgOCupx8OMW1VEq3dEhLer2hO1aCQKWaBWHbrSp/YVkyonBfDFuHnMW2HmUToBfziPNlTKVoSKb8fKcYhZuXaH1+jHt+f5bQxrdnpaHwKBgQCgvLNI7QBx/Qq4vNY+YLqslSHmJky/2u2rrgp10eE2mKIm0IazVWYL329PKuc4HWJPZTGwW7L2+ClKQqQAJ0zQFl3a7ziRkgOy5512CeV4Hm5pTJXJNsjHHpWp/y6j1ONefoV8obHvvVPahHzdHLUZ+bZy/+bYJuIZ3RLriLgasQKBgC0DvuQa2sZEfZQXqUuB1G3vEKTH6Qe0eg5tq/8vaY1tp6QhTmx2CdMHvWRBYoN/6pK85dPyDSskUsYYu/Oqd5K3X9DmrPMFs7XEtu0HvCllRDs66D3JSnCenqBbZW5UGcIbsdjkEtIcLvz0/sb2OZGmkGrDBs0pquly2TxvROWtAoGBALVJrpi+Dk4CGEIfLLpScamEhDcA5ZUM4oOokuVX4b20wYF7MLZU91/9XkQ5RYHH1iuXJNLrhoVDJGOwUU0aVCD1XyeEqz9AUrOeZxc4oYN5ykuszUgD/LTputr7C2u+nEf8Rz0tQcYVyZLOVq8GwEaR7lPpphHm9Yq/5/XUjWGt'
		PrivateKey priKey = getPrivateKeyFromPKCS8(new ByteArrayInputStream(app_private_key.getBytes()));
	}
}




