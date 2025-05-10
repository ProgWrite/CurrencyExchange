package CurrencyExchange.servlet;

import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.dto.ExchangeRatesDto;
import CurrencyExchange.service.CurrencyService;
import CurrencyExchange.service.ExchangeRatesService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();


    //TODO дублирование кода с Gson

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Gson gson = new Gson();
        String json = gson.toJson(exchangeRatesService.findAll());
        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateString = req.getParameter("rate");

        if(isEmpty(baseCurrencyCode) || isEmpty(targetCurrencyCode) || isEmpty(rateString)) {
            sendRequiredFormFieldErrorMessage(resp);
            return;
        }

        if(!isCurrenciesExists(baseCurrencyCode, targetCurrencyCode)){
            sendCurrencyNotExistsMessage(resp);
            return;
        }

        if(isExchangeRateExists(baseCurrencyCode, targetCurrencyCode)){
            sendExchangeRateExistsErrorMessage(resp);
            return;
        }

        BigDecimal rate = new BigDecimal(rateString);

        ExchangeRatesDto exchangeRatesDto = exchangeRatesService.addNewExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);

        if (exchangeRatesDto != null) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(exchangeRatesDto);
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();

    }


    // TODO дублирование CurrenciesServlet
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    // TODO дублирование c классом CurrenciesServlet
    private void sendRequiredFormFieldErrorMessage(HttpServletResponse httpResponse) throws IOException {
        String jsonResponse = "{\"message\": \"The required form field is missing. Enter the baseCurrencyCode, targetCurrencyCode and rate\"}";
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        httpResponse.getWriter().write(jsonResponse);
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

    private void sendExchangeRateExistsErrorMessage(HttpServletResponse httpResponse) throws IOException {
        String jsonResponse = "{\"message\": \"The exchange rate you entered already exists. Please enter another one\"}";
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setStatus(HttpServletResponse.SC_CONFLICT);
        httpResponse.getWriter().write(jsonResponse);
    }

    private boolean isCurrenciesExists(String baseCurrencyCode, String targetCurrencyCode) {

        if (currencyService.getCurrencyByCode(baseCurrencyCode) != null && currencyService.getCurrencyByCode(targetCurrencyCode) != null) {
            return true;
        }
        return false;
    }

    private void sendCurrencyNotExistsMessage(HttpServletResponse httpResponse) throws IOException {
        String jsonResponse = "{\"message\": \"The currency or currencies you entered do not exist. Create currencies and try again. \"}";
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        httpResponse.getWriter().write(jsonResponse);
    }




}
