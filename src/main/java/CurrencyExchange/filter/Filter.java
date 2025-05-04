package CurrencyExchange.filter;

import CurrencyExchange.service.CurrencyService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

//TODO у dmdev было как записать сюда несколько значений
@WebFilter("/*")
public class Filter implements jakarta.servlet.Filter {

    private static final int REQUIRED_LENGTH_FOR_CURRENCY = 3;
    private static final int REQUIRED_LENGTH_FOR_EXCHANGE_RATE = 6;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //TODO может эти 4 строки можно в отдельный метод? Тогда можно переделать метод с валидацией длины. Надо подумать об этом
        //TODO рефакторинг кода где 4 одинаковых строки с httpResponse

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String pathInfo = httpRequest.getPathInfo();

        String requestURI = httpRequest.getRequestURI();


        if (requestURI.startsWith("/currency")) {
            String jsonResponse = "{\"message\": \"Currency code must be " + REQUIRED_LENGTH_FOR_CURRENCY + " characters\"}";
            if (validateStringLength(httpRequest, httpResponse, REQUIRED_LENGTH_FOR_CURRENCY, jsonResponse)) {
                return;
            }
        } else if (requestURI.startsWith("/exchangeRate/")) {
            String jsonResponse = "{\"message\": \"Exchange Rate code must be " + REQUIRED_LENGTH_FOR_EXCHANGE_RATE + " characters\"}";
            if (validateStringLength(httpRequest, httpResponse, REQUIRED_LENGTH_FOR_EXCHANGE_RATE, jsonResponse)) {
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean validateStringLength(HttpServletRequest httpRequest, HttpServletResponse httpResponse, int requiredLength, String jsonResponse) throws IOException {

        String pathInfo = httpRequest.getPathInfo();
        String code = pathInfo.substring(1);

        if (code.length() != requiredLength) {
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.getWriter().write(jsonResponse);
            return true;
        }
        return false;
    }
}

