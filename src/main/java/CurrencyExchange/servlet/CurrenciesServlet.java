package CurrencyExchange.servlet;

import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.service.CurrencyService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet ("/currencies")
public class CurrenciesServlet extends HttpServlet {

    //TODO дублирование кода!

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Gson gson = new Gson();
        String json = gson.toJson(currencyService.findAll());
        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        if (isEmpty(code) || isEmpty(name) || isEmpty(sign)) {
            sendRequiredFormFieldErrorMessage(resp);
            return;
        }

        if(isCurrencyExists(code)) {
            sendCurrencyExistErrorMessage(resp);
            return;
        }

        CurrencyDto currencyDTO = new CurrencyDto(code,name,sign);
        CurrencyDto addedCurrency = currencyService.create(currencyDTO);

        if (addedCurrency != null) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(addedCurrency);
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();

    }

    private void sendRequiredFormFieldErrorMessage(HttpServletResponse httpResponse) throws IOException {
        String jsonResponse = "{\"message\": \"The required form field is missing. Enter the code, name and sign\"}";
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        httpResponse.getWriter().write(jsonResponse);
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public boolean isCurrencyExists(String code) {
        List <CurrencyDto> currencies = currencyService.findAll();
        for (CurrencyDto currency : currencies) {
            if (currency.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    private void sendCurrencyExistErrorMessage(HttpServletResponse httpResponse) throws IOException {
        String jsonResponse = "{\"message\": \"The currency you entered already exists. Please enter another one\"}";
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setStatus(HttpServletResponse.SC_CONFLICT);
        httpResponse.getWriter().write(jsonResponse);
    }

}





