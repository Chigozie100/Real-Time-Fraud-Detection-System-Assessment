package com.Fraud.Detection.System.RealTime.Fraud.Detection.System.service.impl;

import com.Fraud.Detection.System.RealTime.Fraud.Detection.System.entities.Transaction;
import com.Fraud.Detection.System.RealTime.Fraud.Detection.System.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    // thread safe implementation, first thing, first out data structure
    private final ConcurrentLinkedQueue<Transaction> buffer = new ConcurrentLinkedQueue<>();
    private final Map<String, Deque<Transaction>> userHistory = new HashMap<>();
    @Override
    public void processTransaction(List<Transaction> transactions) {
        synchronized (buffer) { // Synchronize on buffer to ensure thread safety
            for (Transaction transaction : transactions) {
                    buffer.offer(transaction);
            }

            while (!buffer.isEmpty()) {
                Transaction current = buffer.poll();
                updateUserHistory(current);
                checkFraudulentPatterns(current);
            }
        }
    }


    // update the transaction history of the user
    private void updateUserHistory(Transaction transaction) {
        String userId = transaction.getUserId();
        Deque<Transaction> userTransactions = userHistory.get(userId);

        // if there is no transaction history for the user, it creates a new empty queue to hold the user transaction
        // and add it to the user history map
        if (userTransactions == null) {
            userTransactions = new LinkedList<>();
            userHistory.put(userId, userTransactions);
        }
        userTransactions.offer(transaction);
    }

    private void checkFraudulentPatterns(Transaction transaction) {
        String userId = transaction.getUserId();
//        ConcurrentHashMap<String, Long> usedServices = new ConcurrentHashMap<>();

        // Check multiple services within 5 minutes
        CompletableFuture.runAsync(()-> {
            ConcurrentHashMap<String, Long> usedServices = new ConcurrentHashMap<>();
            for (Transaction tx : userHistory.get(userId)) {
                if (tx.getTimestamp() + 300000 > transaction.getTimestamp()) { // 5 minutes in milliseconds
                    usedServices.put(tx.getServiceId(), tx.getTimestamp());
                }
            }
            if (usedServices.size() > 3) {
                Optional<Long> max = usedServices.entrySet().stream().map((entry) -> entry.getValue()).max(Long::compareTo);
                Optional<Long> min = usedServices.entrySet().stream().map((entry) -> entry.getValue()).min(Long::compareTo);
                    if(max.get() - min.get() <= 300000) {
                        // Flag for exceeding service limit
                        System.out.println("Alert: User " + userId + " used more than 3 services in 5 minutes!");
                    } else {
                        usedServices.clear();
                    }
                System.out.println(Thread.currentThread().getName());
            }
        }, Executors.newSingleThreadExecutor());

        CompletableFuture.runAsync(()->{
            // Check high transaction amount (assuming no UserRepository)
            double averageAmount = userHistory.get(userId).stream().mapToDouble(Transaction::getAmount).average().orElse(0.0);
            if (transaction.getAmount() > averageAmount * 5) {
                // Flag for high transaction amount
                System.out.println("Alert: User " + userId + " transaction amount exceeds 5x average!");
            }
        });

        CompletableFuture.runAsync(()->{
            // Ping-pong activity logic can be implemented here
            checkPingPongActivity(userId, transaction);
        });
    }

    //Checks where a user alternates between services within a short time frame
    private void checkPingPongActivity(String userId, Transaction transaction) {
        Deque<Transaction> userTransactions = userHistory.get(userId);
        if (userTransactions.size() < 2) {
            return; // Not enough transactions to check ping-pong
        }
        Transaction previous = userTransactions.pollLast();
        if (previous.getServiceId().equals(transaction.getServiceId())
                && previous.getTimestamp() + 600000 > transaction.getTimestamp()) { // 10 minutes in milliseconds
            // Potential ping-pong activity between two services
            System.out.println("Alert: User " + userId + " might be involved in ping-pong activity!");
        }
        userTransactions.offerLast(previous); // Add previous transaction back to history
    }



}
