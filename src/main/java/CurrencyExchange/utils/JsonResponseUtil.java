package CurrencyExchange.utils;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


// TODO возможно этот класс не понадобится после рефакторинга кода
public class JsonResponseUtil {

    public static void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(data);
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }

}
