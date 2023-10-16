package br.com.alura.br.com.alura.ecommerce;

import br.com.alura.ecommerce.Email;
import br.com.alura.ecommerce.KafkaDispatcher;

import javax.servlet.ServletConfig;
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
    private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy()  {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
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

            orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

            var emailCode = new Email("New order received", "Thank you for your order! We are processing your order!");
            emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);
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
