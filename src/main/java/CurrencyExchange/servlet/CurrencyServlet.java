package CurrencyExchange.servlet;

import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.exceptions.NotFoundException;
import CurrencyExchange.service.CurrencyService;
import CurrencyExchange.utils.ErrorResponseHandler;
import CurrencyExchange.utils.JsonResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code  = req.getPathInfo().substring(1);

        try{
            CurrencyDto currencyDto = currencyService.getCurrencyByCode(code);
            objectMapper.writeValue(resp.getWriter(), currencyDto);
        }catch(NotFoundException e){
            ErrorResponseHandler.sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    "Currency doesn't exist! Add a new currency and try again");
        }
    }

}
