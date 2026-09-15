package com.example.loyaltyprogram.controller;

import com.example.loyaltyprogram.dto.request.CreateEarningRuleRequest;
import com.example.loyaltyprogram.dto.request.UpdateEarningRuleRequest;
import com.example.loyaltyprogram.dto.response.EarningRuleResponse;
import com.example.loyaltyprogram.exception.ConflictException;
import com.example.loyaltyprogram.exception.EarningRuleNotFoundException;
import com.example.loyaltyprogram.exception.ProgramExpiredException;
import com.example.loyaltyprogram.exception.ProgramNotFoundException;
import com.example.loyaltyprogram.model.EarningEventType;
import com.example.loyaltyprogram.service.EarningRuleService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EarningRuleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private EarningRuleService earningRuleService;

    @Test
    void createEarningRule_dataCorrect_returnsCreatedRule() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        CreateEarningRuleRequest command = new CreateEarningRuleRequest(EarningEventType.PURCHASE, 10, startDate, endDate);
        EarningRuleResponse response = new EarningRuleResponse(1L, 5L, EarningEventType.PURCHASE, 10, startDate, endDate);
        ArgumentCaptor<CreateEarningRuleRequest> commandCaptor = ArgumentCaptor.forClass(CreateEarningRuleRequest.class);
        when(earningRuleService.createRule(Mockito.eq(5L), any(CreateEarningRuleRequest.class))).thenReturn(response);
        //when + then
        mockMvc.perform(post("/programs/{programId}/earning-rules", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.programId").value(5L))
                .andExpect(jsonPath("$.eventType").value("PURCHASE"))
                .andExpect(jsonPath("$.points").value(10));
        Mockito.verify(earningRuleService).createRule(Mockito.eq(5L), commandCaptor.capture());
        Assertions.assertEquals(EarningEventType.PURCHASE, commandCaptor.getValue().eventType());
        Assertions.assertEquals(10, commandCaptor.getValue().points());
    }

    @Test
    void createEarningRule_programNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        CreateEarningRuleRequest command = new CreateEarningRuleRequest(EarningEventType.PURCHASE, 10, startDate, endDate);
        when(earningRuleService.createRule(Mockito.eq(999L), any(CreateEarningRuleRequest.class)))
                .thenThrow(new ProgramNotFoundException(999L));
        //when + then
        mockMvc.perform(post("/programs/{programId}/earning-rules", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("PROGRAM_NOT_FOUND"))
                .andExpect(jsonPath("$.message", Matchers.containsString("Program not found: id=999")));
    }

    @Test
    void createEarningRule_programExpired_returnsConflictWithMessage() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        CreateEarningRuleRequest command = new CreateEarningRuleRequest(EarningEventType.PURCHASE, 10, startDate, endDate);
        when(earningRuleService.createRule(Mockito.eq(5L), any(CreateEarningRuleRequest.class)))
                .thenThrow(new ProgramExpiredException(5L));
        //when + then
        mockMvc.perform(post("/programs/{programId}/earning-rules", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("PROGRAM_EXPIRED"))
                .andExpect(jsonPath("$.message", Matchers.containsString("Program is not active: id=5")));
    }

    @Test
    void createEarningRule_overlappingPeriod_returnsConflictWithMessage() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        CreateEarningRuleRequest command = new CreateEarningRuleRequest(EarningEventType.PURCHASE, 10, startDate, endDate);
        when(earningRuleService.createRule(Mockito.eq(5L), any(CreateEarningRuleRequest.class)))
                .thenThrow(new ConflictException("EARNING_RULE_OVERLAP",
                        "An active earning rule for event PURCHASE already exists in an overlapping period"));
        //when + then
        mockMvc.perform(post("/programs/{programId}/earning-rules", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("EARNING_RULE_OVERLAP"))
                .andExpect(jsonPath("$.message", Matchers.containsString("overlapping period")));
    }

    @Test
    void listRules_dataCorrect_returnsListOfRules() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        EarningRuleResponse rule1 = new EarningRuleResponse(1L, 5L, EarningEventType.PURCHASE, 10, startDate, endDate);
        EarningRuleResponse rule2 = new EarningRuleResponse(2L, 5L, EarningEventType.SIGN_UP, 50, startDate, endDate);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        when(earningRuleService.listRules(5L)).thenReturn(List.of(rule1, rule2));
        //when + then
        mockMvc.perform(get("/programs/{programId}/earning-rules", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventType").value("PURCHASE"))
                .andExpect(jsonPath("$[1].eventType").value("SIGN_UP"));
        Mockito.verify(earningRuleService).listRules(idCaptor.capture());
        Assertions.assertEquals(5L, idCaptor.getValue());
    }

    @Test
    void listRules_programNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        when(earningRuleService.listRules(999L)).thenThrow(new ProgramNotFoundException(999L));
        //when + then
        mockMvc.perform(get("/programs/{programId}/earning-rules", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Program not found: id=999")));
    }

    @Test
    void getEarningRule_exists_returnsRule() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        EarningRuleResponse response = new EarningRuleResponse(1L, 5L, EarningEventType.REVIEW, 15, startDate, endDate);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        when(earningRuleService.getEarningRule(1L)).thenReturn(response);
        //when + then
        mockMvc.perform(get("/earning-rules/{earningRuleId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.eventType").value("REVIEW"))
                .andExpect(jsonPath("$.points").value(15));
        Mockito.verify(earningRuleService).getEarningRule(idCaptor.capture());
        Assertions.assertEquals(1L, idCaptor.getValue());
    }

    @Test
    void getEarningRule_notExists_returnsNotFoundWithMessage() throws Exception {
        //given
        when(earningRuleService.getEarningRule(1L)).thenThrow(new EarningRuleNotFoundException(1L));
        //when + then
        mockMvc.perform(get("/earning-rules/{earningRuleId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("EARNING_RULE_NOT_FOUND"))
                .andExpect(jsonPath("$.message", Matchers.containsString("Earning rule not found: id=1")));
    }

    @Test
    void updateEarningRule_dataCorrect_returnsUpdatedRule() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        UpdateEarningRuleRequest command = new UpdateEarningRuleRequest(null,25, startDate, endDate);
        EarningRuleResponse response = new EarningRuleResponse(1L, 5L, EarningEventType.PURCHASE, 25, startDate, endDate);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<UpdateEarningRuleRequest> commandCaptor = ArgumentCaptor.forClass(UpdateEarningRuleRequest.class);
        when(earningRuleService.updateEarningRule(anyLong(), any(UpdateEarningRuleRequest.class))).thenReturn(response);
        //when + then
        mockMvc.perform(put("/earning-rules/{earningRuleId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(25));
        Mockito.verify(earningRuleService).updateEarningRule(idCaptor.capture(), commandCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, idCaptor.getValue()),
                () -> Assertions.assertEquals(25, commandCaptor.getValue().points())
        );
    }

    @Test
    void updateEarningRule_notExists_returnsNotFoundWithMessage() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        UpdateEarningRuleRequest command = new UpdateEarningRuleRequest(null, 25, startDate, endDate);
        when(earningRuleService.updateEarningRule(anyLong(), any(UpdateEarningRuleRequest.class)))
                .thenThrow(new EarningRuleNotFoundException(1L));
        //when + then
        mockMvc.perform(put("/earning-rules/{earningRuleId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Earning rule not found: id=1")));
    }

    @Test
    void updateEarningRule_overlappingPeriod_returnsConflictWithMessage() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        UpdateEarningRuleRequest command = new UpdateEarningRuleRequest(null, 25, startDate, endDate);
        when(earningRuleService.updateEarningRule(anyLong(), any(UpdateEarningRuleRequest.class)))
                .thenThrow(new ConflictException("EARNING_RULE_OVERLAP",
                        "An active earning rule for event PURCHASE already exists in an overlapping period"));
        //when + then
        mockMvc.perform(put("/earning-rules/{earningRuleId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("EARNING_RULE_OVERLAP"));
    }

    @Test
    void deleteEarningRule_exists_returnsNoContent() throws Exception {
        //given
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        //when + then
        mockMvc.perform(delete("/earning-rules/{earningRuleId}", 1L))
                .andExpect(status().isNoContent());
        Mockito.verify(earningRuleService).delete(idCaptor.capture());
        Assertions.assertEquals(1L, idCaptor.getValue());
    }

    @Test
    void deleteEarningRule_notExists_returnsNotFoundWithMessage() throws Exception {
        //given
        Mockito.doThrow(new EarningRuleNotFoundException(1L)).when(earningRuleService).delete(1L);
        //when + then
        mockMvc.perform(delete("/earning-rules/{earningRuleId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.containsString("Earning rule not found: id=1")));
    }
}