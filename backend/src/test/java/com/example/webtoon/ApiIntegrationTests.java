package com.example.webtoon;

import com.example.webtoon.domain.Series;
import com.example.webtoon.repo.SeriesRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SeriesRepository seriesRepository;

    @Test
    void registerAndLoginWorks() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().substring(0, 8);
        String email = username + "@example.com";
        String password = "password123";

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value(username));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void protectedEndpointsRejectUnauthenticated() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/users/me/list"))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertThat(status == 401 || status == 403).isTrue();
    }

    @Test
    void listEntryCreateAndUpdate() throws Exception {
        String token = registerAndLogin();
        Series series = createSeries("List Test");

        mockMvc.perform(put("/api/v1/users/me/list/{seriesId}", series.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "READING",
                                  "progress": 3,
                                  "favorite": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READING"))
                .andExpect(jsonPath("$.progress").value(3))
                .andExpect(jsonPath("$.favorite").value(true));

        mockMvc.perform(put("/api/v1/users/me/list/{seriesId}", series.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "COMPLETED",
                                  "progress": 12,
                                  "favorite": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.progress").value(12))
                .andExpect(jsonPath("$.favorite").value(false));

        mockMvc.perform(get("/api/v1/users/me/list/{seriesId}", series.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void favoritesToggleKeepsListEntryWhenProgressExists() throws Exception {
        String token = registerAndLogin();
        Series series = createSeries("Fav Test");

        mockMvc.perform(put("/api/v1/users/me/list/{seriesId}", series.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "READING",
                                  "progress": 2,
                                  "favorite": false
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/users/me/favorites/{seriesId}", series.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/users/me/list/{seriesId}", series.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite").value(true))
                .andExpect(jsonPath("$.progress").value(2));

        mockMvc.perform(delete("/api/v1/users/me/favorites/{seriesId}", series.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/users/me/list/{seriesId}", series.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite").value(false))
                .andExpect(jsonPath("$.progress").value(2));
    }

    @Test
    void ratingsCreateGetAndDelete() throws Exception {
        String token = registerAndLogin();
        Series series = createSeries("Rating Test");

        mockMvc.perform(post("/api/v1/ratings/{seriesId}", series.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "score": 8,
                                  "review": "Great pacing"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(8));

        mockMvc.perform(get("/api/v1/ratings/{seriesId}/me", series.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(8));

        mockMvc.perform(get("/api/v1/ratings/{seriesId}/summary", series.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));

        mockMvc.perform(delete("/api/v1/ratings/{seriesId}", series.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/ratings/{seriesId}/me", series.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    private String registerAndLogin() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().substring(0, 8);
        String email = username + "@example.com";
        String password = "password123";

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, email, password)))
                .andExpect(status().isOk());

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return body.get("token").asText();
    }

    private Series createSeries(String title) {
        Series series = Series.builder()
                .title(title)
                .type("WEBTOON")
                .synopsis("Synopsis")
                .coverImageUrl("https://example.com/cover.jpg")
                .genres(Set.of("Action"))
                .tags(Set.of("Fantasy"))
                .authors(Set.of("Author"))
                .build();
        return seriesRepository.save(series);
    }
}
