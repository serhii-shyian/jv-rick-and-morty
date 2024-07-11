package mate.academy.rickandmorty.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.rickandmorty.dto.external.CharacterResponseDto;
import mate.academy.rickandmorty.dto.external.CharactersDataResponseDto;
import mate.academy.rickandmorty.service.CharacterClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CharacterClientImpl implements CharacterClient {
    private final ObjectMapper objectMapper;
    @Value("${rick-and-morty-url}")
    private String characterUrl;

    @Override
    public List<CharacterResponseDto> findAllCharacters() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(characterUrl))
                .build();
        try {
            HttpResponse<String> response = client.send(
                    request, HttpResponse.BodyHandlers.ofString());
            return parseResponse(response.body()).results();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Can't send request to get data", e);
        }
    }

    private CharactersDataResponseDto parseResponse(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, CharactersDataResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can't parse response body.", e);
        }
    }
}
