package CurrencyExchange.servlet;

import CurrencyExchange.dto.ExchangeConvertDto;
import CurrencyExchange.dto.ExchangeRateRequestDto;
import CurrencyExchange.exceptions.InvalidParameterException;
import CurrencyExchange.service.ExchangeService;
import CurrencyExchange.utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;


@WebServlet("/exchange/*")

public class ExchangeConvertServlet extends HttpServlet {

    private final ExchangeService exchangeService = ExchangeService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountParam = req.getParameter("amount");

        String exchangeRateCode = from + to;
        ExchangeRateRequestDto exchangeRateRequestDto = new ExchangeRateRequestDto(from, to, convertToNumber(amountParam));
        ValidationUtils.validate(exchangeRateRequestDto);

        BigDecimal amount = new BigDecimal(amountParam);
        ExchangeConvertDto exchangeConvertDto = exchangeService.makeExchange(exchangeRateCode, amount);
        objectMapper.writeValue(resp.getWriter(), exchangeConvertDto);
    }

    private static BigDecimal convertToNumber(String amount) {
        if(!amount.matches("-?\\d+(\\.\\d+)?")){
            throw new InvalidParameterException("The amount must contain only digits");
        }
        try {
            return BigDecimal.valueOf(Double.parseDouble(amount));
        }
        catch (NumberFormatException e) {
            throw new InvalidParameterException("Parameter rate must be a number");
        }
    }
}
