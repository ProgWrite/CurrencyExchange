package CurrencyExchange.servlet;


import CurrencyExchange.dto.ExchangeRatesDto;
import CurrencyExchange.exceptions.InvalidParameterException;
import CurrencyExchange.exceptions.NotFoundException;
import CurrencyExchange.service.CurrencyService;
import CurrencyExchange.service.ExchangeRatesService;
import CurrencyExchange.utils.ErrorResponseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Pattern;


@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final static Pattern CHECK_RATE = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        String code = pathInfo.substring(1);

        try{
            ExchangeRatesDto exchangeRatesDto = exchangeRatesService.getExchangeRateByCode(code);
            objectMapper.writeValue(resp.getWriter(), exchangeRatesDto);
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
       String parametr = req.getReader().readLine();
       String rate = parametr.replace("rate=", "");
       BigDecimal rateBigDecimal = convertToNumber(rate);

        if(rateBigDecimal == null){
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
        ExchangeRatesDto exchangeRatesDto = exchangeRatesService.update(correctPathInfo, rateBigDecimal);
        objectMapper.writeValue(resp.getWriter(), exchangeRatesDto);
    }


    private boolean isCurrenciesExists(String baseCurrencyCode, String targetCurrencyCode) {
        if (currencyService.getCurrencyByCode(baseCurrencyCode) != null && currencyService.getCurrencyByCode(targetCurrencyCode) != null) {
            return true;
        }
        return false;
    }

    private boolean isRateCorrect(String rate, HttpServletResponse response) throws IOException {
        if(!CHECK_RATE.matcher(rate).matches()){
            ErrorResponseHandler.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "The exchange rate must contain only numbers or floating point numbers");
        }
        return true;
    }

    private static BigDecimal convertToNumber(String rate) {
        try {
            return BigDecimal.valueOf(Double.parseDouble(rate));
        }
        catch (NumberFormatException e) {
            throw new InvalidParameterException("Parameter rate must be a number");
        }
    }
}









