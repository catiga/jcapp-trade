package com.jeancoder.trade.ready.dto

class PrinterDto {
	String print_code;
	String print_html;
	
	String qrcode;
	
	public PrinterDto(){}
	
	public PrinterDto(String print_code, String print_html) {
		this.print_code = print_code;
		this.print_html = print_html;
	}
}
