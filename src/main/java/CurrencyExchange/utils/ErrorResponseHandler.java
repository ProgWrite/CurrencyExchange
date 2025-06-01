package CurrencyExchange.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ErrorResponseHandler {

    //TODO исчезнет, когда сделаю фильтр)
    public static void sendErrorResponse(HttpServletResponse response, int errorCode, String message) throws IOException {
        String jsonResponse = String.format("{\"message\": \"%s\"}", message);
        response.setStatus(errorCode);
        response.getWriter().write(jsonResponse);

    }
}
