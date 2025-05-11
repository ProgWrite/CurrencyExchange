package CurrencyExchange.servlet;

import CurrencyExchange.dto.ExchangeConvertDto;
import CurrencyExchange.service.CurrencyService;
import CurrencyExchange.service.ExchangeRatesService;
import CurrencyExchange.util.JsonResponseUtil;
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


@WebServlet("/exchange/*")

public class ExchangeConvertServlet extends HttpServlet {

    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountParam = req.getParameter("amount");
        String exchangeRateCode = from + to;

        //TODO этот код часто повторяется!!!
        if (isEmpty(from) || isEmpty(to) || isEmpty(amountParam)) {
            sendRequiredFormFieldErrorMessage(resp);
            return;
        }

        if(!isCurrenciesExist(from, to)) {
            sendCurrencyNotExistsMessage(resp);
            return;
        }

        BigDecimal amount = new BigDecimal(amountParam);
        JsonResponseUtil.sendJsonResponse(resp, exchangeRatesService.makeExchange(exchangeRateCode, amount));
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // TODO дублирование c классом CurrenciesServlet
    private void sendRequiredFormFieldErrorMessage(HttpServletResponse httpResponse) throws IOException {
        String jsonResponse = "{\"message\": \"The required form field is missing. Enter the base currency code, target currency code, amount and try again.\"}";
        httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        httpResponse.getWriter().write(jsonResponse);
    }


    //TODO дублирование(
    private boolean isCurrenciesExist(String baseCurrencyCode, String targetCurrencyCode) {
        if (currencyService.getCurrencyByCode(baseCurrencyCode) != null && currencyService.getCurrencyByCode(targetCurrencyCode) != null) {
            return true;
        }
        return false;
    }

    private void sendCurrencyNotExistsMessage(HttpServletResponse httpResponse) throws IOException {
        String jsonResponse = "{\"message\": \"Base currency or target currency don't exist! Add a new currency and try again \"}";
        httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        httpResponse.getWriter().write(jsonResponse);
    }
}
