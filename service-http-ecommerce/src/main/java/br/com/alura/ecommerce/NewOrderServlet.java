package br.com.alura.ecommerce;

import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {
    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy()  {
        super.destroy();
        orderDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            //Receive parameters sent http get
            var email = req.getParameter("email");
            var amount = new BigDecimal(req.getParameter("ammount")).setScale(2, RoundingMode.HALF_UP);

            var userId = UUID.randomUUID().toString();
            var orderId = UUID.randomUUID().toString();
            var order = new Order(userId, amount, email);

            orderDispatcher.send("ECOMMERCE_NEW_ORDER",
                    email,
                    new CorrelationId(NewOrderServlet.class.getSimpleName()),
                    order);

            var message = "New order sent successfully.";
            System.out.println(message);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(message);

        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }
}
