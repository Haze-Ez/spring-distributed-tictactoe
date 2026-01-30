package org.example.tictactoe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TictactoeApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // Default check: Does the app start?
    }

    @Test
    void shouldReturnLoginPage() throws Exception {
        // Verify the login page loads with a 200 OK status
        this.mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRedirectUnauthenticatedUserToLogin() throws Exception {
        // Verify that trying to access /game WITHOUT a user forces a redirect to /login
        this.mockMvc.perform(get("/game"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "testUser") // Bypass Spring security with mock user
    void shouldReturnGamePage() throws Exception {
        // Verify the game page loads with a 200 OK status
        this.mockMvc.perform(get("/game"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testUser")
    void shouldEndUserSession() throws Exception {
        // Verify the logout redirects with 320 Found status
        this.mockMvc.perform(post("/logout")
                        .with(csrf()))
                        .andExpect(status().is3xxRedirection());
    }

}
