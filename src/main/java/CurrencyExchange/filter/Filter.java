package CurrencyExchange.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@WebFilter("/*")
public class Filter implements jakarta.servlet.Filter {

    private static final int REQUIRED_LENGTH_FOR_CURRENCY = 3;
    private static final int REQUIRED_LENGTH_FOR_EXCHANGE_RATE = 6;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        String pathInfo = httpRequest.getPathInfo();

        if(requestURI.startsWith("/currency")) {
            String currencyCode = pathInfo.substring(1);

            if (currencyCode.length() != REQUIRED_LENGTH_FOR_CURRENCY) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setContentType("application/json");
                httpResponse.setCharacterEncoding("UTF-8");
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                String jsonResponse = "{\"message\": \"Currency code must be " + REQUIRED_LENGTH_FOR_CURRENCY + " characters\"}";
                httpResponse.getWriter().write(jsonResponse);
                return;
            }

        }

        else if(requestURI.startsWith("/exchangeRate/")) {
            String currencyCode = pathInfo.substring(1);

            if (currencyCode.length() != REQUIRED_LENGTH_FOR_EXCHANGE_RATE) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setContentType("application/json");
                httpResponse.setCharacterEncoding("UTF-8");
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                String jsonResponse = "{\"message\": \"Exchange Rate code must be " + REQUIRED_LENGTH_FOR_EXCHANGE_RATE + " characters\"}";
                httpResponse.getWriter().write(jsonResponse);
                return;
            }

        }

        filterChain.doFilter(request, response);
    }
}
