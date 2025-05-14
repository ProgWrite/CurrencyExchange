package CurrencyExchange.servlet;

import CurrencyExchange.dto.ExchangeRatesDto;
import CurrencyExchange.exceptions.NotFoundException;
import CurrencyExchange.service.CurrencyService;
import CurrencyExchange.service.ExchangeRatesService;
import CurrencyExchange.util.ErrorResponseHandler;
import CurrencyExchange.util.JsonResponseUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        String code = pathInfo.substring(1);

        try{
            JsonResponseUtil.sendJsonResponse(resp, exchangeRatesService.getExchangeRateByCode(code));
        }catch(NotFoundException e){
            ErrorResponseHandler.sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    "Exchange rate doesn't exist! Add a new exchange rate and try again");
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if(method.equals("PATCH")) {
            doPatch(req, resp);
        }else if(method.equals("GET")) {
            doGet(req, resp);
        }else{
            super.service(req, resp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String requestBody = sb.toString();
        String[] params = requestBody.split("&");
        BigDecimal rate = null;

        for (String param : params) {
            String[] pair = param.split("=");
            if (pair.length == 2 && "rate".equals(pair[0])) {
                String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8.name());
                rate = new BigDecimal(value);
                break;
            }
        }

        if(rate == null){
            ErrorResponseHandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
            "The required form field is missing. Enter the rate and try again");
            return;
        }

        String pathInfo = req.getPathInfo();
        String correctPathInfo = pathInfo.substring(1);
        String baseCurrencyCode = correctPathInfo.substring(0, 3);
        String targetCurrencyCode = correctPathInfo.substring(3, 6);

        if(!isCurrenciesExists(baseCurrencyCode, targetCurrencyCode)){
            ErrorResponseHandler.sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    "Exchange rate doesn't exist! Add a new exchange rate and try again");
            return;
        }

        JsonResponseUtil.sendJsonResponse(resp, exchangeRatesService.update(correctPathInfo, rate));
    }

    private boolean isCurrenciesExists(String baseCurrencyCode, String targetCurrencyCode) {
        if (currencyService.getCurrencyByCode(baseCurrencyCode) != null && currencyService.getCurrencyByCode(targetCurrencyCode) != null) {
            return true;
        }
        return false;
    }

}









