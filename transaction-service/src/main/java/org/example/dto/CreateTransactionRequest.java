package org.example.dto;

import lombok.*;
import org.example.model.Transaction;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CreateTransactionRequest {

    // SenderId not needed because it is obtained from security context

    @NotBlank
    private String receiverId;

    @Min(1)
    private long transactionAmount; // take in the lowest denominations

    private String reason;

    public Transaction toTransaction(){
        return Transaction.builder()
                .receiverId(this.receiverId)
                .reason(this.reason)
                .transactionAmount(this.transactionAmount)
                .build();
    }
}
