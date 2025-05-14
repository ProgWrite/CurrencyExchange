package CurrencyExchange.servlet;

import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.service.CurrencyService;
import CurrencyExchange.util.ErrorResponseHandler;
import CurrencyExchange.util.JsonResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

@WebServlet ("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final static int REQUIRED_LENGTH = 3;
    private final CurrencyService currencyService = CurrencyService.getInstance();
    Predicate<String> isEmpty = str -> str == null || str.trim().isEmpty();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonResponseUtil.sendJsonResponse(resp, currencyService.findAll());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        if (isEmpty.test(code) || isEmpty.test(name) || isEmpty.test(sign)) {
            ErrorResponseHandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "The required form field is missing. Enter the code, name and sign");
            return;
        }

//        if(code.length() != REQUIRED_LENGTH) {
//            ErrorResponseHandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
//                    "Currency code must be " + REQUIRED_LENGTH + " characters");
//            return;
//        }

        if (isCurrencyExists(code)) {
            ErrorResponseHandler.sendErrorResponse(resp, HttpServletResponse.SC_CONFLICT,
                    "The currency you entered already exists. Please enter another one");
            return;
        }

        CurrencyDto currencyDTO = new CurrencyDto(code,name,sign);
        CurrencyDto addedCurrency = currencyService.create(currencyDTO);

        if (addedCurrency != null) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }
        JsonResponseUtil.sendJsonResponse(resp, addedCurrency);
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

}





