package com.smart.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.entities.PaymentData;

public interface PaymentDataRepository extends JpaRepository<PaymentData, Long> {

	public PaymentData findByOrderId(String orderId);
	
}
