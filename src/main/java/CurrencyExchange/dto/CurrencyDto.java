package CurrencyExchange.dto;


import java.util.Objects;

//TODO нужно будет пересмотреть эту логику (пока что тестовый вариант)
public class CurrencyDto {
    private final long id;
    private final String code;
    private final String fullName;

    public CurrencyDto(long id, String code, String fullName) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
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
                '}';
    }
}
