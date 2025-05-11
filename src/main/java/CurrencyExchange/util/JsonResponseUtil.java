package CurrencyExchange.util;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JsonResponseUtil {

    public static void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(data);
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }

}
