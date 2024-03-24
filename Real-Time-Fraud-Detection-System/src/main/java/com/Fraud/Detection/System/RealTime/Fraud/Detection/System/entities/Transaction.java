package com.Fraud.Detection.System.RealTime.Fraud.Detection.System.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class Transaction implements Serializable {
    private Long timestamp;
    private double amount;
    @JsonProperty("userID")
    private String userId;
    @JsonProperty("serviceID")
    private String serviceId;


}
