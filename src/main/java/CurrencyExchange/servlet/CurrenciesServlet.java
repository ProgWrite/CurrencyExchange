package CurrencyExchange.servlet;

import CurrencyExchange.dto.CurrencyDto;
import CurrencyExchange.service.CurrencyService;
import CurrencyExchange.utils.ErrorResponseHandler;
import CurrencyExchange.utils.JsonResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet ("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final Predicate<String> isEmpty = str -> str == null || str.trim().isEmpty();
    private final static Pattern CHECK_CODE = Pattern.compile("^[A-Z]+$");
    private final static Pattern CHECK_NAME = Pattern.compile("^(?! )[A-Za-z]+( [A-Za-z]+)*$");
    private static final int REQUIRED_LENGTH_FOR_CODE = 3;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
          List<CurrencyDto> currenciesDto = currencyService.findAll();
          objectMapper.writeValue(resp.getWriter(), currenciesDto);
    }


    // TODO валидация исчезнет и методы снизу тоже уберутся!!!!!

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

        if(!isCodeCorrect(code, resp) || !isNameCorrect(name, resp)) {
            return;
        }

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
        objectMapper.writeValue(resp.getWriter(), addedCurrency);
    }



    private boolean isCurrencyExists(String code) {
        List <CurrencyDto> currencies = currencyService.findAll();
        for (CurrencyDto currency : currencies) {
            if (currency.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCodeCorrect(String code, HttpServletResponse response) throws IOException {
        if(code != null && code.length() != REQUIRED_LENGTH_FOR_CODE) {
            ErrorResponseHandler.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Currency code must be " + REQUIRED_LENGTH_FOR_CODE + " characters");
            return false;
        }
        if(!CHECK_CODE.matcher(code).matches()) {
            ErrorResponseHandler.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "The currency code must be without spaces, in upper case and have "
                            + REQUIRED_LENGTH_FOR_CODE + " english alphabet characters");
            return false;
        }
        return true;
    }

    private boolean isNameCorrect(String name, HttpServletResponse response) throws IOException {
        if(!CHECK_NAME.matcher(name).matches()){
            ErrorResponseHandler.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "The currency name must contain letters of the English alphabet and the " +
                            "first character must not be a space");
            return false;
        }
        return true;
    }
}





