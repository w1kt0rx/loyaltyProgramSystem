package com.example.loyaltyprogram.controller;

import com.example.loyaltyprogram.dto.request.CreateRewardRequest;
import com.example.loyaltyprogram.dto.request.UpdateRewardRequest;
import com.example.loyaltyprogram.dto.response.RewardResponse;
import com.example.loyaltyprogram.exception.ProgramExpiredException;
import com.example.loyaltyprogram.exception.ProgramNotFoundException;
import com.example.loyaltyprogram.exception.RewardNotFoundException;
import com.example.loyaltyprogram.service.RewardService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RewardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private RewardService rewardService;

    @Test
    void createReward_dataCorrect_returnsCreatedReward() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        CreateRewardRequest command = new CreateRewardRequest("Voucher 10 PLN", 100, 50, startDate, endDate);
        RewardResponse response = new RewardResponse(1L, 5L, "Voucher 10 PLN", 100, 50);
        ArgumentCaptor<CreateRewardRequest> commandCaptor = ArgumentCaptor.forClass(CreateRewardRequest.class);
        when(rewardService.createReward(eq(5L), any(CreateRewardRequest.class))).thenReturn(response);
        //when + then
        mockMvc.perform(post("/programs/{programId}/rewards", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.programId").value(5L))
                .andExpect(jsonPath("$.name").value("Voucher 10 PLN"))
                .andExpect(jsonPath("$.pointsCost").value(100))
                .andExpect(jsonPath("$.availableQuantity").value(50));

        Mockito.verify(rewardService).createReward(eq(5L), commandCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals("Voucher 10 PLN", commandCaptor.getValue().name()),
                () -> Assertions.assertEquals(100, commandCaptor.getValue().pointsCost()),
                () -> Assertions.assertEquals(50, commandCaptor.getValue().availableQuantity())
        );
    }

    @Test
    void createReward_programNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        CreateRewardRequest command = new CreateRewardRequest("Voucher 10 PLN", 100, 50, startDate, endDate);
        when(rewardService.createReward(eq(999L), any(CreateRewardRequest.class)))
                .thenThrow(new ProgramNotFoundException(999L));
        //when + then
        mockMvc.perform(post("/programs/{programId}/rewards", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("PROGRAM_NOT_FOUND"))
                .andExpect(jsonPath("$.message", Matchers.containsString("Program not found: id=999")));
    }

    @Test
    void createReward_programExpired_returnsConflictWithMessage() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        CreateRewardRequest command = new CreateRewardRequest("Voucher 10 PLN", 100, 50, startDate, endDate);
        when(rewardService.createReward(eq(5L), any(CreateRewardRequest.class)))
                .thenThrow(new ProgramExpiredException(5L));
        //when + then
        mockMvc.perform(post("/programs/{programId}/rewards", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("PROGRAM_EXPIRED"))
                .andExpect(jsonPath("$.message", Matchers.containsString("Program is not active: id=5")));
    }

    @Test
    void listRewards_dataCorrect_returnsListOfRewards() throws Exception {
        //given
        RewardResponse r1 = new RewardResponse(1L, 5L, "Voucher 10 PLN", 100, 50);
        RewardResponse r2 = new RewardResponse(2L, 5L, "Voucher 20 PLN", 200, 30);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        when(rewardService.listRewards(5L)).thenReturn(List.of(r1, r2));
        //when + then
        mockMvc.perform(get("/programs/{programId}/rewards", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Voucher 10 PLN"))
                .andExpect(jsonPath("$[0].pointsCost").value(100))
                .andExpect(jsonPath("$[0].availableQuantity").value(50))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Voucher 20 PLN"))
                .andExpect(jsonPath("$[1].pointsCost").value(200))
                .andExpect(jsonPath("$[1].availableQuantity").value(30));

        Mockito.verify(rewardService).listRewards(idCaptor.capture());
        Assertions.assertEquals(5L, idCaptor.getValue());
    }

    @Test
    void listRewards_programNotExists_returnsNotFoundWithMessage() throws Exception {
        //given
        when(rewardService.listRewards(999L)).thenThrow(new ProgramNotFoundException(999L));
        //when + then
        mockMvc.perform(get("/programs/{programId}/rewards", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("PROGRAM_NOT_FOUND"))
                .andExpect(jsonPath("$.message", Matchers.containsString("Program not found: id=999")));
    }

    @Test
    void getReward_exists_returnsReward() throws Exception {
        //given
        RewardResponse response = new RewardResponse(1L, 5L, "Voucher 10 PLN", 100, 50);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        when(rewardService.getReward(1L)).thenReturn(response);
        //when + then
        mockMvc.perform(get("/rewards/{rewardId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Voucher 10 PLN"))
                .andExpect(jsonPath("$.pointsCost").value(100))
                .andExpect(jsonPath("$.availableQuantity").value(50));

        Mockito.verify(rewardService).getReward(idCaptor.capture());
        Assertions.assertEquals(1L, idCaptor.getValue());
    }

    @Test
    void getReward_notExists_returnsNotFoundWithMessage() throws Exception {
        //given
        when(rewardService.getReward(1L)).thenThrow(new RewardNotFoundException(1L));
        //when + then
        mockMvc.perform(get("/rewards/{rewardId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("REWARD_NOT_FOUND"))
                .andExpect(jsonPath("$.message", Matchers.containsString("Reward not found: id=1")));
    }

    @Test
    void updateReward_dataCorrect_returnsUpdatedReward() throws Exception {
        //given
        UpdateRewardRequest command = new UpdateRewardRequest("Voucher 15 PLN", 150, 40);
        RewardResponse response = new RewardResponse(1L, 5L, "Voucher 15 PLN", 150, 40);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<UpdateRewardRequest> commandCaptor = ArgumentCaptor.forClass(UpdateRewardRequest.class);
        when(rewardService.updateReward(eq(1L), any(UpdateRewardRequest.class))).thenReturn(response);
        //when + then
        mockMvc.perform(put("/rewards/{rewardId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Voucher 15 PLN"))
                .andExpect(jsonPath("$.pointsCost").value(150))
                .andExpect(jsonPath("$.availableQuantity").value(40));

        Mockito.verify(rewardService).updateReward(idCaptor.capture(), commandCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, idCaptor.getValue()),
                () -> Assertions.assertEquals("Voucher 15 PLN", commandCaptor.getValue().name()),
                () -> Assertions.assertEquals(150, commandCaptor.getValue().pointsCost()),
                () -> Assertions.assertEquals(40, commandCaptor.getValue().availableQuantity())
        );
    }

    @Test
    void updateReward_notExists_returnsNotFoundWithMessage() throws Exception {
        //given
        UpdateRewardRequest command = new UpdateRewardRequest("Voucher 15 PLN", 150, 40);
        when(rewardService.updateReward(eq(1L), any(UpdateRewardRequest.class)))
                .thenThrow(new RewardNotFoundException(1L));
        //when + then
        mockMvc.perform(put("/rewards/{rewardId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("REWARD_NOT_FOUND"))
                .andExpect(jsonPath("$.message", Matchers.containsString("Reward not found: id=1")));
    }

    @Test
    void deleteReward_exists_returnsNoContent() throws Exception {
        //given
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        //when + then
        mockMvc.perform(delete("/rewards/{rewardId}", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(rewardService).deleteReward(idCaptor.capture());
        Assertions.assertEquals(1L, idCaptor.getValue());
    }

    @Test
    void deleteReward_notExists_returnsNotFoundWithMessage() throws Exception {
        //given
        Mockito.doThrow(new RewardNotFoundException(1L)).when(rewardService).deleteReward(1L);
        //when + then
        mockMvc.perform(delete("/rewards/{rewardId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("REWARD_NOT_FOUND"))
                .andExpect(jsonPath("$.message", Matchers.containsString("Reward not found: id=1")));
    }
}