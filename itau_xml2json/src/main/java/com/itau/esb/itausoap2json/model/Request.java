package com.itau.esb.itausoap2json.model;

import java.util.List;

public class Request {

	private IssuedIdent IssuedIdent;
	private List<Contact> ContactList;

	public List<Contact> getContactList() {
		return ContactList;
	}

	public void setContactList(List<Contact> contactList) {
		ContactList = contactList;
	}

	public IssuedIdent getIssuedIdent() {
		return IssuedIdent;
	}

	public void setIssuedIdent(IssuedIdent IssuedIdent) {
		this.IssuedIdent = IssuedIdent;
	}

	@Override
	public String toString() {
		return "ClassPojo [ContactList = " + ContactList + ", IssuedIdent = " + IssuedIdent + "]";
	}
}
