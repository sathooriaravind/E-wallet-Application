package org.example.dto;

import lombok.*;

@Getter@Setter@Builder@NoArgsConstructor@AllArgsConstructor
public class UpdateWalletRequest {

    private Long balance;

    private String currency;

    private String walletId;

}
