package CurrencyExchange.servlet;

import CurrencyExchange.entity.ExchangeRates;
import CurrencyExchange.service.ExchangeRatesService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();

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
}
