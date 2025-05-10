package CurrencyExchange.dto;


import java.util.Objects;

//TODO нужно будет пересмотреть эту логику (пока что тестовый вариант)
public class CurrencyDto {
    private final long id;
    private final String code;
    private final String name;
    private final String sign;

    public CurrencyDto(long id, String code, String name, String sign) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.sign = sign;
    }


    //TODO возможно это полная херня, надо смотреть
    public CurrencyDto(String code, String name, String sign) {
        this(0L, code, name, sign);
    }




    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyDto that = (CurrencyDto) o;
        return id == that.id && Objects.equals(code, that.code) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name);
    }

    @Override
    public String toString() {
        return "CurrencyDto{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
