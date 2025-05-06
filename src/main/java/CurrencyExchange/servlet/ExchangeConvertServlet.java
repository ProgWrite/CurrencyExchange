package CurrencyExchange.servlet;

import CurrencyExchange.dto.ExchangeConvertDto;
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


@WebServlet("/exchange/*")

public class ExchangeConvertServlet extends HttpServlet {

    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

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

        Gson gson = new Gson();
        ExchangeConvertDto exchangeConvertDto = exchangeRatesService.convert(exchangeRateCode, amount);
        String json = gson.toJson(exchangeConvertDto);
        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();

    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // TODO дублирование c классом CurrenciesServlet
    private void sendRequiredFormFieldErrorMessage(HttpServletResponse httpResponse) throws IOException {
        String jsonResponse = "{\"message\": \"The required form field is missing. Enter the base currency code, target currency code, amount and try again.\"}";
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
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
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        httpResponse.getWriter().write(jsonResponse);
    }




}
