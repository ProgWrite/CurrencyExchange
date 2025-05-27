package CurrencyExchange.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Currencies {
    private Long id;
    private String code;
    private String name;
    private String sign;
}
