package CurrencyExchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyDto {
    private final long id;
    private final String code;
    private final String name;
    private final String sign;


    public CurrencyDto(String code, String name, String sign) {
        this(0L, code, name, sign);
    }
}
