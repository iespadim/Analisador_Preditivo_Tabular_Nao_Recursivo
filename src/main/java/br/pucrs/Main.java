package br.pucrs;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class Main {
    static String listaDeArquivosEArgumentos = "";

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);

        desenhaApp(result -> {
            listaDeArquivosEArgumentos = result;
            latch.countDown(); // Signal that the result is available.
        });

        try {
            latch.await(); // Wait for the result to be available.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(listaDeArquivosEArgumentos);
        // Add more processing here if needed.
    }

    public static void desenhaApp(Consumer<String> callback) {
        SwingUtilities.invokeLater(() -> new GrammarInputUI(callback));
    }
}
