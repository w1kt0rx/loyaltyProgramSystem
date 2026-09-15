package com.example.loyaltyprogram.controller;

import com.example.loyaltyprogram.dto.PageDto;
import com.example.loyaltyprogram.dto.request.EarnPointsRequest;
import com.example.loyaltyprogram.dto.request.PageRequestDto;
import com.example.loyaltyprogram.dto.response.EarnPointsResponse;
import com.example.loyaltyprogram.dto.response.PointsHistoryResponse;
import com.example.loyaltyprogram.exception.*;
import com.example.loyaltyprogram.model.EarningEventType;
import com.example.loyaltyprogram.service.PointsService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PointsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private PointsService pointsService;

    @Test
    void earn_dataCorrect_returnsEarnPointsResponse() throws Exception {
        // given
        Long userId = 1L;
        EarnPointsRequest request = new EarnPointsRequest(EarningEventType.PURCHASE, 5L, null, "ref-123");
        EarnPointsResponse response = new EarnPointsResponse(100L, "PURCHASE", 10, 150, "ref-123");
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<EarnPointsRequest> requestCaptor = ArgumentCaptor.forClass(EarnPointsRequest.class);
        when(pointsService.earnPoints(eq(userId), any(EarnPointsRequest.class))).thenReturn(response);
        // when + then
        mockMvc.perform(post("/users/{userId}/points/earn", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(100L))
                .andExpect(jsonPath("$.eventType").value("PURCHASE"))
                .andExpect(jsonPath("$.pointsEarned").value(10))
                .andExpect(jsonPath("$.newBalance").value(150))
                .andExpect(jsonPath("$.referenceId").value("ref-123"));

        Mockito.verify(pointsService).earnPoints(userIdCaptor.capture(), requestCaptor.capture());
        Assertions.assertEquals(userId, userIdCaptor.getValue());
        Assertions.assertEquals("ref-123", requestCaptor.getValue().referenceId());
    }

    @Test
    void history_dataCorrect_returnsPageOfTransactions() throws Exception {
        // given
        Long userId = 1L;
        Long programId = 5L;
        PointsHistoryResponse item1 = new PointsHistoryResponse(101L, "EARN", 20, "Earned via rule PURCHASE", "Gold Program", LocalDateTime.of(2026, 3, 14, 12, 0));
        PointsHistoryResponse item2 = new PointsHistoryResponse(100L, "EARN", 10, "Earned via rule PURCHASE", "Gold Program", LocalDateTime.of(2026, 3, 10, 10, 0));
        PageDto<PointsHistoryResponse> pageResponse = new PageDto<>(List.of(item1, item2), 0, 10, 2L, 1);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> programIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<PageRequestDto> pageRequestCaptor = ArgumentCaptor.forClass(PageRequestDto.class);
        when(pointsService.getHistory(eq(userId), eq(programId), any(PageRequestDto.class))).thenReturn(pageResponse);
        // when + then
        mockMvc.perform(get("/users/{userId}/points/history", userId)
                        .param("programId", programId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(101L))
                .andExpect(jsonPath("$.content[0].type").value("EARN"))
                .andExpect(jsonPath("$.content[0].points").value(20))
                .andExpect(jsonPath("$.content[0].description").value("Earned via rule PURCHASE"))
                .andExpect(jsonPath("$.content[0].programName").value("Gold Program"))
                .andExpect(jsonPath("$.content[1].id").value(100L))
                .andExpect(jsonPath("$.totalElements").value(2));

        Mockito.verify(pointsService).getHistory(userIdCaptor.capture(), programIdCaptor.capture(), pageRequestCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(userId, userIdCaptor.getValue()),
                () -> Assertions.assertEquals(programId, programIdCaptor.getValue()),
                () -> Assertions.assertEquals(0, pageRequestCaptor.getValue().page()),
                () -> Assertions.assertEquals(10, pageRequestCaptor.getValue().size())
        );
    }

    @Test
    void earn_invalidPayload_returnsBadRequest() throws Exception {
        // given
        Long userId = 1L;
        EarnPointsRequest request = new EarnPointsRequest(EarningEventType.PURCHASE, 5L, 10L, "ref-123");
        when(pointsService.earnPoints(eq(userId), any(EarnPointsRequest.class)))
                .thenThrow(new InvalidRequestException("Provide either (eventType and programId) or earningRuleId, not both or neither"));
        // when + then
        mockMvc.perform(post("/users/{userId}/points/earn", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.containsString("Provide either (eventType and programId) or earningRuleId")));
    }

    @Test
    void earn_userNotBelongToProgram_returnsForbidden() throws Exception {
        // given
        Long userId = 1L;
        EarnPointsRequest request = new EarnPointsRequest(EarningEventType.PURCHASE, 5L, null, "ref-123");
        when(pointsService.earnPoints(eq(userId), any(EarnPointsRequest.class)))
                .thenThrow(new ForbiddenOperationException("User id=" + userId + " does not belong to program id=5"));
        // when + then
        mockMvc.perform(post("/users/{userId}/points/earn", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", Matchers.containsString("does not belong to program")));
    }

    @Test
    void earn_userNotFound_returnsNotFound() throws Exception {
        // given
        Long userId = 999L;
        EarnPointsRequest request = new EarnPointsRequest(EarningEventType.PURCHASE, 5L, null, "ref-123");
        when(pointsService.earnPoints(eq(userId), any(EarnPointsRequest.class)))
                .thenThrow(new UserNotFoundException(userId));
        // when + then
        mockMvc.perform(post("/users/{userId}/points/earn", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.message", Matchers.containsString("User not found: id=999")));
    }

    @Test
    void earn_userDeactivated_returnsConflict() throws Exception {
        // given
        Long userId = 1L;
        EarnPointsRequest request = new EarnPointsRequest(EarningEventType.PURCHASE, 5L, null, "ref-123");
        when(pointsService.earnPoints(eq(userId), any(EarnPointsRequest.class)))
                .thenThrow(new ConflictException("USER_DEACTIVATED", "User id=1 is deactivated"));
        // when + then
        mockMvc.perform(post("/users/{userId}/points/earn", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("USER_DEACTIVATED"))
                .andExpect(jsonPath("$.message", Matchers.containsString("is deactivated")));
    }

    @Test
    void earn_programExpired_returnsConflict() throws Exception {
        // given
        Long userId = 1L;
        EarnPointsRequest request = new EarnPointsRequest(EarningEventType.PURCHASE, 5L, null, "ref-123");
        when(pointsService.earnPoints(eq(userId), any(EarnPointsRequest.class)))
                .thenThrow(new ProgramExpiredException(5L));
        // when + then
        mockMvc.perform(post("/users/{userId}/points/earn", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("PROGRAM_EXPIRED"))
                .andExpect(jsonPath("$.message", Matchers.containsString("Program is not active: id=5")));
    }

    @Test
    void earn_noEarningRule_returnsConflict() throws Exception {
        // given
        Long userId = 1L;
        EarnPointsRequest request = new EarnPointsRequest(EarningEventType.PURCHASE, 5L, null, "ref-123");
        when(pointsService.earnPoints(eq(userId), any(EarnPointsRequest.class)))
                .thenThrow(new NoEarningRuleException(5L, "PURCHASE"));
        // when + then
        mockMvc.perform(post("/users/{userId}/points/earn", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("NO_EARNING_RULE"));
    }

    @Test
    void history_userNotBelongToProgram_returnsForbidden() throws Exception {
        // given
        Long userId = 1L;
        Long programId = 5L;
        when(pointsService.getHistory(eq(userId), eq(programId), any(PageRequestDto.class)))
                .thenThrow(new ForbiddenOperationException("User id=1 does not belong to program id=5"));
        // when + then
        mockMvc.perform(get("/users/{userId}/points/history", userId)
                        .param("programId", programId.toString()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", Matchers.containsString("does not belong to program")));
    }
}