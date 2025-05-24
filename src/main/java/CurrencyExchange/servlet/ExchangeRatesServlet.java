package CurrencyExchange.servlet;

import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.dto.ExchangeRatesDto;
import CurrencyExchange.service.CurrencyService;
import CurrencyExchange.service.ExchangeRatesService;
import CurrencyExchange.util.ErrorResponseHandler;
import CurrencyExchange.util.JsonResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    Predicate<String> isEmpty = str -> str == null || str.trim().isEmpty();
    private final static Pattern CHECK_RATE = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonResponseUtil.sendJsonResponse(resp, exchangeRatesService.findAll());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateString = req.getParameter("rate");

        if(isEmpty.test(baseCurrencyCode) || isEmpty.test(targetCurrencyCode) || isEmpty.test(rateString)) {
            ErrorResponseHandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "The required form field is missing. Enter the baseCurrencyCode, targetCurrencyCode and rate");
            return;
        }

        if(!isCurrenciesExists(baseCurrencyCode, targetCurrencyCode)){
            ErrorResponseHandler.sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    "The currency or currencies you entered do not exist. Create currencies and try again.");
            return;
        }

        if(isExchangeRateExists(baseCurrencyCode, targetCurrencyCode)){
            ErrorResponseHandler.sendErrorResponse(resp, HttpServletResponse.SC_CONFLICT,
                    "The exchange rate you entered already exists. Please enter another one");
            return;
        }

        if(!isRateCorrect(rateString, resp)){
            return;
        }

        BigDecimal rate = new BigDecimal(rateString);
        ExchangeRatesDto exchangeRatesDto = exchangeRatesService.create(baseCurrencyCode, targetCurrencyCode, rate);

        if (exchangeRatesDto != null) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }
        JsonResponseUtil.sendJsonResponse(resp, exchangeRatesDto);

    }

    private boolean isExchangeRateExists(String baseCurrencyCode, String targetCurrencyCode) {
        List<ExchangeRatesDto> exchangeRates = exchangeRatesService.findAll();
        CurrencyDto baseCurrency = currencyService.getCurrencyByCode(baseCurrencyCode);
        CurrencyDto targetCurrency = currencyService.getCurrencyByCode(targetCurrencyCode);
        String baseCode = baseCurrency.getCode();
        String targetCode = targetCurrency.getCode();

        for(ExchangeRatesDto exchangeRateDto : exchangeRates){
            CurrencyDto checkBaseCurrency = exchangeRateDto.getBaseCurrency();
            CurrencyDto checkTargetCurrency = exchangeRateDto.getTargetCurrency();
            String checkBaseCode = checkBaseCurrency.getCode();
            String checkTargetCode = checkTargetCurrency.getCode();

            if(baseCode.equals(checkBaseCode) && targetCode.equals(checkTargetCode)){
                return true;
            }
        }
        return false;
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
}
