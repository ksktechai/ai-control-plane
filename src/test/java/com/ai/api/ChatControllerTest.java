package com.ai.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ai.control.ControlPlane;
import com.ai.domain.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private ControlPlane controlPlane;

    @Test
    void shouldHandleChatRequest() throws Exception {
        Answer answer = new Answer("AI is artificial intelligence", List.of(), "llama3.1:8b");
        VerificationResult verification =
                new VerificationResult(VerificationStatus.GROUNDED, List.of(), 0.95, "Grounded");
        AnswerResult result = new AnswerResult(answer, verification, 0.9, "SIMPLE");

        when(controlPlane.answer(any(Question.class))).thenReturn(result);

        mockMvc.perform(
                        post("/api/chat")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"question\":\"What is AI?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("AI is artificial intelligence"))
                .andExpect(jsonPath("$.confidence").value(0.9));
    }

    @Test
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
