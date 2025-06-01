package CurrencyExchange.servlet;


import CurrencyExchange.dto.ExchangeRatesDto;
import CurrencyExchange.exceptions.InvalidParameterException;
import CurrencyExchange.service.CurrencyService;
import CurrencyExchange.service.ExchangeRatesService;
import CurrencyExchange.utils.ErrorResponseHandler;
import CurrencyExchange.utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;


@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String exchangeRatecode = pathInfo.substring(1);
        ValidationUtils.checkLength(exchangeRatecode);

        String baseCurrencyCode = exchangeRatecode.substring(0, 3);
        String targetCurrencyCode = exchangeRatecode.substring(3);
        ValidationUtils.validateCurrencyCode(baseCurrencyCode);
        ValidationUtils.validateCurrencyCode(targetCurrencyCode);

        ExchangeRatesDto exchangeRatesDto = exchangeRatesService.getExchangeRateByCode(exchangeRatecode);
        objectMapper.writeValue(resp.getWriter(), exchangeRatesDto);

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

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currencyCodes = req.getPathInfo().replaceFirst("/", "");
        ValidationUtils.checkLength(currencyCodes);

        String baseCurrencyCode = currencyCodes.substring(0, 3);
        String targetCurrencyCode = currencyCodes.substring(3, 6);

        ValidationUtils.validateCurrencyCode(baseCurrencyCode);
        ValidationUtils.validateCurrencyCode(targetCurrencyCode);



        String parameter = req.getReader().readLine();

        if (parameter == null || !parameter.contains("rate")) {
            throw new InvalidParameterException("Missing parameter - rate");
        }

        String rate = parameter.replace("rate=", "");

        if (rate.isBlank()) {
            throw new InvalidParameterException("Missing parameter - rate");
        }

        BigDecimal rateBigDecimal = convertToNumber(rate);

        String pathInfo = req.getPathInfo();
        String correctPathInfo = pathInfo.substring(1);
        ExchangeRatesDto exchangeRatesDto = exchangeRatesService.update(correctPathInfo, rateBigDecimal);
        objectMapper.writeValue(resp.getWriter(), exchangeRatesDto);
    }


    private static BigDecimal convertToNumber(String rate) {
        try {
            BigDecimal rateBigDecimal = BigDecimal.valueOf(Double.parseDouble(rate));
            if (rateBigDecimal.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidParameterException("Invalid parameter - rate must be non-negative");
            }
            return rateBigDecimal;
        }
        catch (NumberFormatException e) {
            throw new InvalidParameterException("Parameter rate must be a number");
        }
    }
}









