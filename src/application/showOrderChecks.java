package application;

import java.sql.Date;

public class showOrderChecks {

	private int payID;
	private int amount;
	private Date payDate;
	private Date dueDate;
	private int cID;
	private String cname;
	private int phoneNum;
	
	@SuppressWarnings("exports")
	public showOrderChecks(int payID, int amount, Date payDate, Date dueDate, int cID, String cname, int phoneNum)
	{
		this.payID=payID;
		this.amount=amount;
		this.payDate=payDate;
		this.dueDate=dueDate;
		this.cID=cID;
		this.cname=cname;
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
	
	public void setCID(int cID)
	{
		this.cID=cID;
	}
	public int getCID()
	{
		return this.cID;
	}
	
	public void setCname(String cname)
	{
		this.cname=cname;
	}
	public String getCname()
	{
		return this.cname;
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
