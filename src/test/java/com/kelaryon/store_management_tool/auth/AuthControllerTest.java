package com.kelaryon.store_management_tool.auth;

import com.kelaryon.store_management_tool.data.LoginResponseDTO;
import com.kelaryon.store_management_tool.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void cleanDb() {
        accountRepository.deleteAll();
    }

    @Test
    void signupTest() throws Exception {

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "email":"john@example.com",
                                        "password":"password123"
                                    }
                                """))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        Assertions.assertTrue(accountRepository.existsByEmail("john@example.com"));
    }

    @Test
    void loginTest() throws Exception {

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "email":"john@example.com",
                                        "password":"password123"
                                    }
                                """))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "email":"john@example.com",
                                        "password":"password123"
                                    }
                                """))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        LoginResponseDTO response =
                objectMapper.readValue(
                        mvcResult.getResponse().getContentAsString(),
                        LoginResponseDTO.class
                );
        String token = response.token();
        System.out.println("JWT: " + token);
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isBlank());
    }
}