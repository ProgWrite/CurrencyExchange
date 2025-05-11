package CurrencyExchange.servlet;

import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.exceptions.NotFoundException;
import CurrencyExchange.service.CurrencyService;
import CurrencyExchange.util.JsonResponseUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;




@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String code = pathInfo.substring(1);

        try{
            JsonResponseUtil.sendJsonResponse(resp, currencyService.getCurrencyByCode(code));
        }catch(NotFoundException e){
            sendCurrencyNotExistsMessage(resp);
        }
    }

    private void sendCurrencyNotExistsMessage(HttpServletResponse httpResponse) throws IOException {
        String jsonResponse = "{\"message\": \"Currency doesn't exist! Add a new currency and try again \"}";
        httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        httpResponse.getWriter().write(jsonResponse);
    }

}
