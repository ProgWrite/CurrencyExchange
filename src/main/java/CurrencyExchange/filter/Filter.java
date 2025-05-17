package CurrencyExchange.filter;

import CurrencyExchange.util.ErrorResponseHandler;
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

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();




        if (requestURI.startsWith("/currency")) {
            String jsonResponse = "Currency code must be " + REQUIRED_LENGTH_FOR_CURRENCY + " characters";
            if (isStringLengthValid(httpRequest, httpResponse, REQUIRED_LENGTH_FOR_CURRENCY, jsonResponse)) {
                return;
            }
        }else if (requestURI.startsWith("/exchangeRate/")) {
            String jsonResponse = "Exchange Rate code must be " + REQUIRED_LENGTH_FOR_EXCHANGE_RATE + " characters";
            if (isStringLengthValid(httpRequest, httpResponse, REQUIRED_LENGTH_FOR_EXCHANGE_RATE, jsonResponse)) {
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isStringLengthValid(HttpServletRequest httpRequest, HttpServletResponse httpResponse, int requiredLength, String jsonResponse) throws IOException {
        String pathInfo = httpRequest.getPathInfo();
        String code = pathInfo.substring(1);

        if (code.length() != requiredLength) {
            ErrorResponseHandler.sendErrorResponse(httpResponse, HttpServletResponse.SC_BAD_REQUEST, jsonResponse);
            return true;
        }
        return false;
    }

}

