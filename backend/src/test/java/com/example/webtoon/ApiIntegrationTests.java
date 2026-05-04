package com.example.webtoon;

import com.example.webtoon.domain.Series;
import com.example.webtoon.repo.SeriesRepository;
import com.example.webtoon.security.AuthCookieService;
import com.example.webtoon.security.CsrfCookieService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

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
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("HttpOnly")));

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("HttpOnly")))
                .andReturn();

        mockMvc.perform(get("/api/v1/auth/me")
                        .cookie(toAuthCookies(loginResult).authCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString(CsrfCookieService.CSRF_COOKIE_NAME)));
    }

    @Test
    void protectedEndpointsRejectUnauthenticated() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/users/me/list"))
                .andReturn();

        int status = result.getResponse().getStatus();
        assertThat(status == 401 || status == 403).isTrue();
    }

    @Test
    void catalogWritesRequireAdminRole() throws Exception {
        AuthCookies authCookies = registerAndLogin();

        mockMvc.perform(post("/api/v1/series")
                        .with(auth(authCookies))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "User Submitted Series",
                                  "type": "WEBTOON",
                                  "synopsis": "Should require moderation",
                                  "coverImageUrl": "https://example.com/cover.jpg",
                                  "genres": ["Action"],
                                  "tags": ["Fantasy"],
                                  "authors": ["Author"]
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void unsafeCookieRequestRejectsMissingCsrfToken() throws Exception {
        AuthCookies authCookies = registerAndLogin();
        Series series = createSeries("CSRF Test");

        mockMvc.perform(post("/api/v1/ratings/{seriesId}", series.getId())
                        .cookie(authCookies.authCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "score": 8
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void validationRejectsWeakRegistrationPayload() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "bad username",
                                  "email": "not-an-email",
                                  "password": "short"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.username").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void listEntryCreateAndUpdate() throws Exception {
        AuthCookies authCookies = registerAndLogin();
        Series series = createSeries("List Test");

        mockMvc.perform(put("/api/v1/users/me/list/{seriesId}", series.getId())
                        .with(auth(authCookies))
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
                        .with(auth(authCookies))
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
                        .cookie(authCookies.authCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void favoritesToggleKeepsListEntryWhenProgressExists() throws Exception {
        AuthCookies authCookies = registerAndLogin();
        Series series = createSeries("Fav Test");

        mockMvc.perform(put("/api/v1/users/me/list/{seriesId}", series.getId())
                        .with(auth(authCookies))
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
                        .with(auth(authCookies)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/users/me/list/{seriesId}", series.getId())
                        .cookie(authCookies.authCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite").value(true))
                .andExpect(jsonPath("$.progress").value(2));

        mockMvc.perform(delete("/api/v1/users/me/favorites/{seriesId}", series.getId())
                        .with(auth(authCookies)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/users/me/list/{seriesId}", series.getId())
                        .cookie(authCookies.authCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite").value(false))
                .andExpect(jsonPath("$.progress").value(2));
    }

    @Test
    void ratingsCreateGetAndDelete() throws Exception {
        AuthCookies authCookies = registerAndLogin();
        Series series = createSeries("Rating Test");

        mockMvc.perform(post("/api/v1/ratings/{seriesId}", series.getId())
                        .with(auth(authCookies))
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
                        .cookie(authCookies.authCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(8));

        mockMvc.perform(get("/api/v1/ratings/{seriesId}/summary", series.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));

        mockMvc.perform(delete("/api/v1/ratings/{seriesId}", series.getId())
                        .with(auth(authCookies)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/ratings/{seriesId}/summary", series.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0));

        mockMvc.perform(get("/api/v1/ratings/{seriesId}/me", series.getId())
                        .cookie(authCookies.authCookie()))
                .andExpect(status().isNoContent());
    }

    @Test
    void socialListsArePaginated() throws Exception {
        AuthUser alice = registerAndLogin("alice_" + UUID.randomUUID().toString().substring(0, 8));
        AuthUser bob = registerAndLogin("bob_" + UUID.randomUUID().toString().substring(0, 8));

        mockMvc.perform(post("/api/v1/users/{username}/follow", bob.username())
                        .with(auth(alice.authCookies())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/users/{username}/followers?page=0&size=1", bob.username()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value(alice.username()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(1));

        mockMvc.perform(get("/api/v1/users/{username}/following?page=0&size=1", alice.username()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value(bob.username()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(1));

        mockMvc.perform(get("/api/v1/users/{username}/followers?sort=username,asc", bob.username()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad request"));
    }

    private AuthCookies registerAndLogin() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().substring(0, 8);
        return registerAndLogin(username).authCookies();
    }

    private AuthUser registerAndLogin(String username) throws Exception {
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

        return new AuthUser(username, toAuthCookies(loginResult));
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

    private RequestPostProcessor auth(AuthCookies authCookies) {
        return request -> {
            request.setCookies(authCookies.authCookie(), authCookies.csrfCookie());
            request.addHeader(CsrfCookieService.CSRF_HEADER_NAME, authCookies.csrfCookie().getValue());
            return request;
        };
    }

    private AuthCookies toAuthCookies(MvcResult result) {
        return new AuthCookies(
                extractCookie(result, AuthCookieService.AUTH_COOKIE_NAME),
                extractCookie(result, CsrfCookieService.CSRF_COOKIE_NAME)
        );
    }

    private Cookie extractCookie(MvcResult result, String cookieName) {
        Cookie cookie = result.getResponse().getCookie(cookieName);
        assertThat(cookie).isNotNull();
        return new Cookie(cookie.getName(), cookie.getValue());
    }

    private record AuthCookies(Cookie authCookie, Cookie csrfCookie) {
    }

    private record AuthUser(String username, AuthCookies authCookies) {
    }
}
