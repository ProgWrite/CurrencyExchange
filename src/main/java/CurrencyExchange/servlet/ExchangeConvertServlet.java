package CurrencyExchange.servlet;

import CurrencyExchange.dto.ExchangeConvertDto;
import CurrencyExchange.dto.ExchangeRatesDto;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String exchangeRateCode = from + to;
        BigDecimal amount = new BigDecimal(req.getParameter("amount"));

        Gson gson = new Gson();
        ExchangeConvertDto exchangeConvertDto = exchangeRatesService.convert(exchangeRateCode, amount);
        String json = gson.toJson(exchangeConvertDto);
        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();
    }
}
