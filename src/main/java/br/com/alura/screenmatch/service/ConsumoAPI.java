package br.com.alura.screenmatch.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsumoAPI {

    public String obterDados(String endereco) {
        HttpClient client = HttpClient.newHttpClient(); //httpClient é como se fosse um cliente
        HttpRequest request = HttpRequest.newBuilder() // cria URI que direciona o endereço que terá requisição
                .uri(URI.create(endereco))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client // manda requisição e manda resposta
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String json = response.body(); //corpo da resposta
        return json;
    }
}
