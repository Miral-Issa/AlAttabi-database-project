package application;

import java.sql.Date;

public class PurchasePayment {
	private int paymentID;
	private String type;
	private int amount;
	private Date paymentDate;
	private Date dueDate;
	private int purID;
	
	@SuppressWarnings("exports")
	public PurchasePayment(int paymentID, String type, int amount, Date paymentDate, Date dueDate, int purID)
	{
		this.paymentID=paymentID;
		this.type=type;
		this.amount=amount;
		this.paymentDate=paymentDate;
		this.dueDate=dueDate;
		this.purID=purID;
	}
	
	public void setPaymentID(int paymentID)
	{
		this.paymentID=paymentID;
	}
	public int getPaymentID()
	{
		return this.paymentID;
	}
	
	public void setType(String type)
	{
		this.type=type;
	}
	public String getType()
	{
		return this.type;
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
	public void setPaymentDate(Date paymentDate)
	{
		this.paymentDate=paymentDate;
	}
	@SuppressWarnings("exports")
	public Date getPaymentDate()
	{
		return this.paymentDate;
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
	
	public void setPurID(int purID)
	{
		this.purID=purID;
	}
	public int getPurID()
	{
		return this.purID;
	}
}
