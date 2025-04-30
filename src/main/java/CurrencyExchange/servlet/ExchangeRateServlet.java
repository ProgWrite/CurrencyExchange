package CurrencyExchange.servlet;

import CurrencyExchange.dto.ExchangeRatesDto;
import CurrencyExchange.service.ExchangeRatesService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Gson gson = new Gson();
        String code = pathInfo.substring(1);

        String json = gson.toJson(exchangeRatesService.getExchangeRateByCode(code));
        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if(!method.equals("PATCH")) {
            super.service(req, resp);
        }
        this.doPatch(req, resp);
    }


    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String requestBody = sb.toString();
        String[] params = requestBody.split("&");
        BigDecimal rate = null;

        for (String param : params) {
            String[] pair = param.split("=");
            if (pair.length == 2 && "rate".equals(pair[0])) {
                String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8.name());
                rate = new BigDecimal(value);
                break;
            }
        }


        String pathInfo = req.getPathInfo();
        String correctPathInfo = pathInfo.substring(1);
        ExchangeRatesDto updatedExchangeRate = exchangeRatesService.updateExchangeRate(correctPathInfo, rate);

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(updatedExchangeRate);
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse);
        out.flush();

    }
}









