package CurrencyExchange.servlet;

import CurrencyExchange.dto.ExchangeConvertDto;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;
import java.util.regex.Pattern;


@WebServlet("/exchange/*")

public class ExchangeConvertServlet extends HttpServlet {

    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    Predicate<String> isEmpty = str -> str == null || str.trim().isEmpty();
    private final static Pattern CHECK_AMOUNT = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountParam = req.getParameter("amount");
        String exchangeRateCode = from + to;


        if (isEmpty.test(from) || isEmpty.test(to) || isEmpty.test(amountParam)) {
            ErrorResponseHandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "The required form field is missing. Enter the base currency code, target currency code, amount and try again.");
            return;
        }

        if(!isCurrenciesExist(from, to)) {
            ErrorResponseHandler.sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    "Base currency or target currency don't exist! Add a new currency and try again");
            return;
        }

        if(!isAmountCorrect(amountParam, resp)){
            return;
        }

        BigDecimal amount = new BigDecimal(amountParam);
        JsonResponseUtil.sendJsonResponse(resp, exchangeRatesService.makeExchange(exchangeRateCode, amount));
    }

    private boolean isCurrenciesExist(String baseCurrencyCode, String targetCurrencyCode) {
        if (currencyService.getCurrencyByCode(baseCurrencyCode) != null && currencyService.getCurrencyByCode(targetCurrencyCode) != null) {
            return true;
        }
        return false;
    }

    private boolean isAmountCorrect(String amount, HttpServletResponse response) throws IOException {
        if(!CHECK_AMOUNT.matcher(amount).matches()){
            ErrorResponseHandler.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Amount must contain only numbers or floating point numbers");
            return false;
        }
        return true;
    }


}
