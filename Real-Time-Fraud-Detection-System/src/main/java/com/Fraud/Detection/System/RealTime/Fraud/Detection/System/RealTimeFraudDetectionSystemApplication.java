package com.Fraud.Detection.System.RealTime.Fraud.Detection.System;

import com.Fraud.Detection.System.RealTime.Fraud.Detection.System.entities.Transaction;
import com.Fraud.Detection.System.RealTime.Fraud.Detection.System.service.TransactionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class RealTimeFraudDetectionSystemApplication{
	@Autowired
	private TransactionService transactionService;
	public static void main(String[] args) {SpringApplication.run(RealTimeFraudDetectionSystemApplication.class, args);
	}

	// Simulate real-time data processing by reading transactions from a file line by line (replace with actual data source)
	@PostConstruct
	public void processTestData() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<Transaction>> typeReference = new TypeReference<>(){

		};
		InputStream inputStream = TypeReference.class.getResourceAsStream("/transactions.json");
		try {
			List<Transaction> transactions = mapper.readValue(inputStream, typeReference);
			System.out.println(transactions);
			transactionService.processTransaction(transactions);
		} catch (Exception e){
			System.out.println(e.getMessage());
		}

	}


}
