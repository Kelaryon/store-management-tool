package com.kelaryon.store_management_tool.auth;

import com.kelaryon.store_management_tool.data.AccountToken;
import com.kelaryon.store_management_tool.data.LoginResponseDTO;
import com.kelaryon.store_management_tool.data.TokenRefreshResponseDTO;
import com.kelaryon.store_management_tool.repository.AccountRepository;
import com.kelaryon.store_management_tool.repository.AccountTokenRepository;
import com.kelaryon.store_management_tool.security.AuthUtils;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
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
    @Autowired
    private AccountTokenRepository accountTokenRepository;
    @Autowired
    private AuthUtils authUtils;

    @BeforeEach
    void cleanDb() {
        accountTokenRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void signupTest() throws Exception {

        //Valid account creation test
        signupAPICall("""
                    {
                        "email":"john@example.com",
                        "password":"PasWord2@--D"
                    }
                """, status().isOk());

        Assertions.assertTrue(accountRepository.existsByEmail("john@example.com"));

        //Invalid Email format Test
        signupAPICall("""
                    {
                        "email":"vailedEmail",
                        "password":"PasWord2@--D"
                    }
                """, status().isBadRequest());

        //Invalid no email field Test
        signupAPICall("""
                    {
                        "password":"PasWord2@--D"
                    }
                """, status().isBadRequest());

        //Invalid account creation password test
        signupAPICall("""
                    {
                        "email":"john@example.com"
                    }
                """, status().isBadRequest());

        //Invalid account creation password test
        signupAPICall("""
                    {
                        "email":"john@example.com",
                        "password":"Pas123"
                    }
                """, status().isBadRequest());
    }

    @Test
    void signupDuplicateTest() throws Exception {

        signupAPICall("""
                    {
                        "email":"john@example.com",
                        "password":"password123"
                    }
                """, status().isOk());
        Assertions.assertTrue(accountRepository.existsByEmail("john@example.com"));
        signupAPICall("""
                    {
                        "email":"john@example.com",
                        "password":"password123"
                    }
                """, status().isConflict());
    }

    @Test
    void loginTest() throws Exception {

        signupAPICall("""
                    {
                        "email":"john@example.com",
                        "password":"Password123"
                    }
                """, status().isOk());

        MvcResult mvcResult = loginAPICall("""
                    {
                        "email":"john@example.com",
                        "password":"Password123"
                    }
                """);
        LoginResponseDTO response = convertMVCResponseToObject(mvcResult, LoginResponseDTO.class);
        String accessToken = response.accessToken();
        Assertions.assertTrue(StringUtils.isNotBlank(accessToken));
        String refreshTokenString = response.refreshToken();
        Assertions.assertTrue(StringUtils.isNotBlank(refreshTokenString));
        Long accountIdFromRefreshToken = authUtils.getAccountIdFromRefreshToken(refreshTokenString);
        AccountToken refreshToken = accountTokenRepository.findActiveTokenByAccountIdAndTokenType(accountIdFromRefreshToken, "refreshToken");
        Assertions.assertTrue(authUtils.encodingMatches(refreshTokenString, refreshToken.getTokenHash()));
        MvcResult mvcResult2 = loginAPICall("""
                    {
                        "email":"john@example.com",
                        "password":"Password123"
                    }
                """);
        LoginResponseDTO response2 = convertMVCResponseToObject(mvcResult2, LoginResponseDTO.class);
        String refreshTokenString2 = response2.refreshToken();
        Assertions.assertTrue(StringUtils.isNotBlank(refreshTokenString2));
        Long accountIdFromRefreshToken2 = authUtils.getAccountIdFromRefreshToken(refreshTokenString2);
        AccountToken refreshToken2 = accountTokenRepository.findActiveTokenByAccountIdAndTokenType(accountIdFromRefreshToken2, "refreshToken");
        Assertions.assertTrue(authUtils.encodingMatches(refreshTokenString2, refreshToken2.getTokenHash()));
        Assertions.assertNotEquals(refreshToken.getTokenHash(), refreshToken2.getTokenHash());
    }

    @Test
    void refreshToken() throws Exception {
        signupAPICall("""
                    {
                        "email":"john@example.com",
                        "password":"Password123"
                    }
                """, status().isOk());
        MvcResult mvcResult = loginAPICall("""
                    {
                        "email":"john@example.com",
                        "password":"Password123"
                    }
                """);
        //Verify Refresh Token Test
        LoginResponseDTO loginResponseDTO = convertMVCResponseToObject(mvcResult, LoginResponseDTO.class);
        String refreshTokenString = loginResponseDTO.refreshToken();
        MvcResult refreshTokenAPICallResponse = refreshTokenAPICall("{\"refreshToken\":" + "\"" + refreshTokenString + "\"}",status().isOk());
        TokenRefreshResponseDTO tokenRefreshResponseDTO = convertMVCResponseToObject(refreshTokenAPICallResponse, TokenRefreshResponseDTO.class);
        Assertions.assertNotEquals(loginResponseDTO.accessToken(),tokenRefreshResponseDTO.accessToken());
        Assertions.assertNotEquals(loginResponseDTO.refreshToken(),tokenRefreshResponseDTO.refreshToken());
        ////Verify Refresh Token multiple refreshes requests
        MvcResult refreshTokenAPICallResponse2 = refreshTokenAPICall("{\"refreshToken\":" + "\"" + tokenRefreshResponseDTO.refreshToken() + "\"}",status().isOk());
        TokenRefreshResponseDTO tokenRefreshResponseDTO2 = convertMVCResponseToObject(refreshTokenAPICallResponse2, TokenRefreshResponseDTO.class);
        refreshTokenAPICall("{\"refreshToken\":" + "\"" + tokenRefreshResponseDTO2.refreshToken() + "\"}",status().isOk());
        //Verify Refresh Token with invalid token test -> used the first token
        refreshTokenAPICall("{\"refreshToken\":" + "\"" + refreshTokenString + "\"}",status().isUnauthorized());
    }

    private @NonNull MvcResult loginAPICall(String content) throws Exception {
        return mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    private @NonNull MvcResult refreshTokenAPICall(String content, ResultMatcher resultMatcher) throws Exception {
        return mockMvc.perform(post("/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(resultMatcher)
                .andReturn();
    }

    private void signupAPICall(String content, ResultMatcher resultMatcher) throws Exception {
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(resultMatcher);
    }

    private <T> T convertMVCResponseToObject(MvcResult result, Class<T> clazz) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                clazz
        );
    }
}