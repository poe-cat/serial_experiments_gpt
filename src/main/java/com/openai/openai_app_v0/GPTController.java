package com.openai.gpt3_app_v0;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@RestController
@CacheConfig(cacheNames = "texts")
public class GPTController {
    private String generatedText;
    private final String API_KEY = "";

    @Cacheable
    @PostMapping("/generate")
    public void generateText(@RequestBody Map<String, Object> request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(request);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            String url = "https://api.openai.com/v1/engines/davinci/completions";
            String jsonResponse = restTemplate.postForObject(url, entity, String.class);
            GPTResponse response = objectMapper.readValue(jsonResponse, GPTResponse.class);
            generatedText = response.getChoices().get(0).getText();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Cacheable
    @GetMapping("/generated")
    public String getGeneratedText() {
        return generatedText;
    }

    @CacheEvict(allEntries = true)
    @PostMapping("/clearCache")
    public void clearCache() {}
}


