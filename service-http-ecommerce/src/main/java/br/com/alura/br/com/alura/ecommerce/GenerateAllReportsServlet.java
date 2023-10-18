package br.com.alura.br.com.alura.ecommerce;

import br.com.alura.ecommerce.KafkaDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class GenerateAllReportsServlet extends HttpServlet {
    private final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        batchDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            batchDispatcher.send("SEND_MESSAGE_TO_ALL_USERS","USER_GENERATE_READING_REPORT", "USER_GENERATE_READING_REPORT");

            System.out.println("Sent generate report to all users");

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("Report requests generated");

        } catch (InterruptedException e) {
            throw new ServletException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
