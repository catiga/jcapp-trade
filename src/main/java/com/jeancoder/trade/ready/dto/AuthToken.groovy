package com.jeancoder.trade.ready.dto

class AuthToken {

	AuthUser user;
	
	AccountSession session;
	
	List<AuthRole> roles;
	
	List<SysFunction> functions;
	
	public AuthToken() {}
	
	public AuthToken(AuthUser user, AccountSession session) {
		this.user = user;
		this.session = session;
	}

	public AuthUser getUser() {
		return user;
	}

	public void setUser(AuthUser user) {
		this.user = user;
	}

	public AccountSession getSession() {
		return session;
	}

	public void setSession(AccountSession session) {
		this.session = session;
	}

	public List<AuthRole> getRoles() {
		return roles;
	}

	public void setRoles(List<AuthRole> roles) {
		this.roles = roles;
	}

	public List<SysFunction> getFunctions() {
		return functions;
	}

	public void setFunctions(List<SysFunction> functions) {
		this.functions = functions;
	}
	
}
