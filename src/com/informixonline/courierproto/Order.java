package com.informixonline.courierproto;

//Я: На клиента вытягиваются только заказы имеющие статус isperformed = false & order_state = не готов

public class Order {

	/**
	 * @return the aNo
	 */
	public String getaNo() {
		return aNo;
	}
	/**
	 * @param aNo the aNo to set
	 */
	public void setaNo(String aNo) {
		this.aNo = aNo;
	}
	/**
	 * @return the displayNo
	 */
	public String getDisplayNo() {
		return displayNo;
	}
	/**
	 * @param displayNo the displayNo to set
	 */
	public void setDisplayNo(String displayNo) {
		this.displayNo = displayNo;
	}
	/**
	 * @return the aCash
	 */
	public String getaCash() {
		return aCash;
	}
	/**
	 * @param aCash the aCash to set
	 */
	public void setaCash(String aCash) {
		this.aCash = aCash;
	}
	/**
	 * @return the aAddress
	 */
	public String getaAddress() {
		return aAddress;
	}
	/**
	 * @param aAddress the aAddress to set
	 */
	public void setaAddress(String aAddress) {
		this.aAddress = aAddress;
	}
	/**
	 * @return the client
	 */
	public String getClient() {
		return client;
	}
	/**
	 * @param client the client to set
	 */
	public void setClient(String client) {
		this.client = client;
	}
	/**
	 * @return the timeB
	 */
	public String getTimeB() {
		return timeB;
	}
	/**
	 * @param timeB the timeB to set
	 */
	public void setTimeB(String timeB) {
		this.timeB = timeB;
	}
	/**
	 * @return the timeE
	 */
	public String getTimeE() {
		return timeE;
	}
	/**
	 * @param timeE the timeE to set
	 */
	public void setTimeE(String timeE) {
		this.timeE = timeE;
	}
	/**
	 * @return the tdd
	 */
	public String getTdd() {
		return tdd;
	}
	/**
	 * @param tdd the tdd to set
	 */
	public void setTdd(String tdd) {
		this.tdd = tdd;
	}
	/**
	 * @return the cont
	 */
	public String getCont() {
		return Cont;
	}
	/**
	 * @param cont the cont to set
	 */
	public void setCont(String cont) {
		Cont = cont;
	}
	/**
	 * @return the contPhone
	 */
	public String getContPhone() {
		return ContPhone;
	}
	/**
	 * @param contPhone the contPhone to set
	 */
	public void setContPhone(String contPhone) {
		ContPhone = contPhone;
	}
	/**
	 * @return the packs
	 */
	public String getPacks() {
		return Packs;
	}
	/**
	 * @param packs the packs to set
	 */
	public void setPacks(String packs) {
		Packs = packs;
	}
	/**
	 * @return the wt
	 */
	public String getWt() {
		return Wt;
	}
	/**
	 * @param wt the wt to set
	 */
	public void setWt(String wt) {
		Wt = wt;
	}
	/**
	 * @return the volWt
	 */
	public String getVolWt() {
		return VolWt;
	}
	/**
	 * @param volWt the volWt to set
	 */
	public void setVolWt(String volWt) {
		VolWt = volWt;
	}
	/**
	 * @return the rems
	 */
	public String getRems() {
		return Rems;
	}
	/**
	 * @param rems the rems to set
	 */
	public void setRems(String rems) {
		Rems = rems;
	}
	/**
	 * @return the ordStatus
	 */
	public String getOrdStatus() {
		return ordStatus;
	}
	/**
	 * @param ordStatus the ordStatus to set
	 */
	public void setOrdStatus(String ordStatus) {
		this.ordStatus = ordStatus;
	}
	/**
	 * @return the ordType
	 */
	public String getOrdType() {
		return ordType;
	}
	/**
	 * @param ordType the ordType to set
	 */
	public void setOrdType(String ordType) {
		this.ordType = ordType;
	}
	/**
	 * @return the recType
	 */
	public String getRecType() {
		return recType;
	}
	/**
	 * @param recType the recType to set
	 */
	public void setRecType(String recType) {
		this.recType = recType;
	}
	/**
	 * @return the isready
	 */
	public String getIsready() {
		return isready;
	}
	/**
	 * @param isready the isready to set
	 */
	public void setIsready(String isready) {
		this.isready = isready;
	}
	/**
	 * @return the inway
	 */
	public String getInway() {
		return inway;
	}
	/**
	 * @param inway the inway to set
	 */
	public void setInway(String inway) {
		this.inway = inway;
	}
	/**
	 * @return the isview
	 */
	public String getIsview() {
		return isview;
	}
	/**
	 * @param isview the isview to set
	 */
	public void setIsview(String isview) {
		this.isview = isview;
	}
	/**
	 * @return the rcpn
	 */
	public String getRcpn() {
		return rcpn;
	}
	/**
	 * @param rcpn the rcpn to set
	 */
	public void setRcpn(String rcpn) {
		this.rcpn = rcpn;
	}
	
	String aNo;
	String displayNo;
	String aCash;
	String aAddress;
	String client;
	String timeB;
	String timeE;
	String tdd;
	String Cont;
	String ContPhone;
	String Packs;
	String Wt;
	String VolWt;
	String Rems;
	String ordStatus;
	String ordType;
	String recType;
	String isready;
	String inway;
	String isview;
	String rcpn;
	
}
