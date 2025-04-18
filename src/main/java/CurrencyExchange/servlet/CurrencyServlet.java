package CurrencyExchange.servlet;

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

@WebServlet ("/currencies")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Gson gson = new Gson();
        String json = gson.toJson(currencyService.findAll());
        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();
    }
}

//TODO текстовое представление. Убрать потом
//try (var printWriter = resp.getWriter()) {
//        printWriter.write("<h1>List of currencies:</h1>");
//            printWriter.write("<ul>");
//            currencyService.findAll().forEach(currencyDto -> {
//        printWriter.write("""
//                        <li>
//                            <a href="/tickets?flightId=%d">%s %s</a>
//                        </li>
//
//                        """.formatted(currencyDto.getId(), currencyDto.getCode(), currencyDto.getFullName()
//                        ));
//                                });
//                                printWriter.write("</ul>");
//        }



