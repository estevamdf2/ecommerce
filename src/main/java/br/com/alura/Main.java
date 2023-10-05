package br.com.alura;

import br.com.alura.ecommerce.NewOrder;

import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        System.out.println("Hello world!");
        NewOrder order = new NewOrder();
        order.sendMessage();
    }
}