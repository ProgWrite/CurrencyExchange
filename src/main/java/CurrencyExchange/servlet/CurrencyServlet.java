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



@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    //TODO есть дублирование в методе getByCode. Также заглушка в виде NUll!

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Gson gson = new Gson();
        String code = pathInfo.substring(1);

        CurrencyDto currencyDto = currencyService.getCurrencyByCode(code);
        if (currencyDto == null) {
            sendCurrencyNotExistsMessage(resp);
            return;
        }

        String json = gson.toJson(currencyDto);
        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();
    }

    private void sendCurrencyNotExistsMessage(HttpServletResponse httpResponse) throws IOException {
        String jsonResponse = "{\"message\": \"Currency doesn't exist! Add a new currency and try again \"}";
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        httpResponse.getWriter().write(jsonResponse);
    }


}
