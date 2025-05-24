package CurrencyExchange;

import java.util.regex.Pattern;

public class MainTest {
    private final static Pattern PATTERN = Pattern.compile("^[A-Z]+$");

    public static void main(String[] args) {
        System.out.println(check("abc"));
        System.out.println(check("RUB"));
    }

    public static boolean check(String s) {
        return PATTERN.matcher(s).matches();
    }
}
