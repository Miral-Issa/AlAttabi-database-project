package application;

import java.sql.Date;

public class showPurChecks {

	private int payID;
	private int amount;
	private Date payDate;
	private Date dueDate;
	private int sID;
	private String sname;
	private int phoneNum;
	
	@SuppressWarnings("exports")
	public showPurChecks(int payID, int amount, Date payDate, Date dueDate, int cID, String cname, int phoneNum)
	{
		this.payID=payID;
		this.amount=amount;
		this.payDate=payDate;
		this.dueDate=dueDate;
		this.sID=cID;
		this.sname=cname;
		this.phoneNum=phoneNum;
	}
	
	public void setPayID(int payID)
	{
		this.payID=payID;
	}
	public int getPayID()
	{
		return this.payID;
	}
	
	public void setAmount(int amount)
	{
		this.amount=amount;
	}
	public int getAmount()
	{
		return this.amount;
	}
	
	@SuppressWarnings("exports")
	public void setPayDate(Date payDate)
	{
		this.payDate=payDate;
	}
	@SuppressWarnings("exports")
	public Date getPayDate()
	{
		return this.payDate;
	}
	
	@SuppressWarnings("exports")
	public void setDueDate(Date dueDate)
	{
		this.dueDate=dueDate;
	}
	@SuppressWarnings("exports")
	public Date getDueDate()
	{
		return this.dueDate;
	}
	
	public void setSID(int sID)
	{
		this.sID=sID;
	}
	public int getSID()
	{
		return this.sID;
	}
	
	public void setSname(String sname)
	{
		this.sname=sname;
	}
	public String getSname()
	{
		return this.sname;
	}
	
	public void setPhoneNum(int phoneNum)
	{
		this.phoneNum=phoneNum;
	}
	public int getPhoneNum()
	{
		return this.phoneNum;
	}
}
