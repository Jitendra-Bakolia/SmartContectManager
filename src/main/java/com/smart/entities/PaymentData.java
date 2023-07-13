package com.smart.entities;

import java.sql.Time;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ManyToAny;

@Entity
public class PaymentData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long MyOrderId;

	private String orderId;

	private int amount;

	private String recipt;

	private String status;

	@ManyToOne
	private User user;

	private String paymentId;

	private LocalDateTime time;

	public PaymentData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public long getMyOrderId() {
		return MyOrderId;
	}

	public void setMyOrderId(long myOrderId) {
		MyOrderId = myOrderId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getRecipt() {
		return recipt;
	}

	public void setRecipt(String recipt) {
		this.recipt = recipt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public PaymentData(long myOrderId, String orderId, int amount, String recipt, String status, User user,
			String paymentId, LocalDateTime time) {
		super();
		MyOrderId = myOrderId;
		this.orderId = orderId;
		this.amount = amount;
		this.recipt = recipt;
		this.status = status;
		this.user = user;
		this.paymentId = paymentId;
		this.time = time;
	}

	@Override
	public String toString() {
		return "PaymentData [MyOrderId=" + MyOrderId + ", orderId=" + orderId + ", amount=" + amount + ", recipt="
				+ recipt + ", status=" + status + ", user=" + user + ", paymentId=" + paymentId + ", time=" + time
				+ "]";
	}

}
