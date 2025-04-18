package CurrencyExchange.dto;


import java.util.Objects;

//TODO нужно будет пересмотреть эту логику (пока что тестовый вариант)
public class CurrencyDto {
    private final long id;
    private final String code;
    private final String fullName;
    private final String sign;

    public CurrencyDto(long id, String code, String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }



    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyDto that = (CurrencyDto) o;
        return id == that.id && Objects.equals(code, that.code) && Objects.equals(fullName, that.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, fullName);
    }

    @Override
    public String toString() {
        return "CurrencyDto{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", fullName='" + fullName + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
