package CurrencyExchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyDto {
    private long id;
    private String code;
    private String name;
    private String sign;

    public CurrencyDto(String code, String name, String sign) {
        this(0L, code, name, sign);
    }
}
