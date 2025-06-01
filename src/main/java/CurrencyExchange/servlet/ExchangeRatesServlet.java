package CurrencyExchange.servlet;

import CurrencyExchange.dto.ExchangeRateRequestDto;
import CurrencyExchange.dto.ExchangeRatesDto;
import CurrencyExchange.exceptions.InvalidParameterException;
import CurrencyExchange.service.ExchangeRatesService;
import CurrencyExchange.utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ExchangeRatesDto> exchangeRatesDto = exchangeRatesService.findAll();
        objectMapper.writeValue(resp.getWriter(), exchangeRatesDto);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateString = req.getParameter("rate");

        if (rateString == null || rateString.isBlank()) {
            throw new InvalidParameterException("Missing parameter - rate");
        }

        ExchangeRateRequestDto exchangeRateRequestDto = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, convertToNumber(rateString));
        ValidationUtils.validate(exchangeRateRequestDto);

        ExchangeRatesDto exchangeRatesDto = exchangeRatesService.create(baseCurrencyCode, targetCurrencyCode, convertToNumber(rateString));

        resp.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(resp.getWriter(), exchangeRatesDto);
    }

    private static BigDecimal convertToNumber(String rate) {
        try {
            return BigDecimal.valueOf(Double.parseDouble(rate));
        }
        catch (NumberFormatException e) {
            throw new InvalidParameterException("Parameter rate must be a number");
        }
    }

}
