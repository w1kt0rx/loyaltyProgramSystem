package com.example.loyaltyprogram.controller;

import com.example.loyaltyprogram.dto.PageDto;
import com.example.loyaltyprogram.dto.request.CreateProgramRequest;
import com.example.loyaltyprogram.dto.request.PageRequestDto;
import com.example.loyaltyprogram.dto.request.UpdateProgramRequest;
import com.example.loyaltyprogram.dto.response.ProgramResponse;
import com.example.loyaltyprogram.exception.ConflictException;
import com.example.loyaltyprogram.exception.ProgramNotFoundException;
import com.example.loyaltyprogram.service.ProgramService;
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
public class ProgramControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ProgramService programService;

    @Test
    void createProgram_dataCorrect_returnsCreatedProgram() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        CreateProgramRequest command = new CreateProgramRequest("Gold Program", "desc", startDate, endDate);
        ProgramResponse response = new ProgramResponse(1L, "Gold Program", "desc", startDate, endDate);
        ArgumentCaptor<CreateProgramRequest> commandCaptor = ArgumentCaptor.forClass(CreateProgramRequest.class);
        when(programService.createProgram(any(CreateProgramRequest.class))).thenReturn(response);
        //when + then
        mockMvc.perform(post("/programs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Gold Program"))
                .andExpect(jsonPath("$.description").value("desc"));
        Mockito.verify(programService).createProgram(commandCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals("Gold Program", commandCaptor.getValue().name()),
                () -> Assertions.assertEquals("desc", commandCaptor.getValue().description())
        );
    }

    @Test
    void createProgram_nameConflict_returnsConflictWithMessage() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        CreateProgramRequest command = new CreateProgramRequest("Gold Program", "desc", startDate, endDate);
        when(programService.createProgram(any(CreateProgramRequest.class)))
                .thenThrow(new ConflictException("PROGRAM_NAME_EXISTS", "Program with name 'Gold Program' already exists"));
        //when + then
        mockMvc.perform(post("/programs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("PROGRAM_NAME_EXISTS"))
                .andExpect(jsonPath("$.message", Matchers.containsString("already exists")));
    }

    @Test
    void getPrograms_dataCorrect_returnsPagedPrograms() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        ProgramResponse p1 = new ProgramResponse(1L, "Gold Program", "desc1", startDate, endDate);
        ProgramResponse p2 = new ProgramResponse(2L, "Silver Program", "desc2", startDate, endDate);
        PageDto<ProgramResponse> pageResponse = new PageDto<>(List.of(p1, p2), 0, 10, 2L, 1);
        ArgumentCaptor<Boolean> activeCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Boolean> includeExpiredCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<PageRequestDto> pageRequestCaptor = ArgumentCaptor.forClass(PageRequestDto.class);
        when(programService.listPrograms(eq(true), eq(false), any(PageRequestDto.class))).thenReturn(pageResponse);

        //when + then
        mockMvc.perform(get("/programs")
                        .param("active", "true")
                        .param("includeExpired", "false")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Gold Program"))
                .andExpect(jsonPath("$.content[0].description").value("desc1"))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].name").value("Silver Program"))
                .andExpect(jsonPath("$.content[1].description").value("desc2"))
                .andExpect(jsonPath("$.totalElements").value(2));

        Mockito.verify(programService).listPrograms(activeCaptor.capture(), includeExpiredCaptor.capture(), pageRequestCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(true, activeCaptor.getValue()),
                () -> Assertions.assertEquals(false, includeExpiredCaptor.getValue())
        );
    }

    @Test
    void getProgram_exists_returnsProgram() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        ProgramResponse response = new ProgramResponse(1L, "Gold Program", "desc", startDate, endDate);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        when(programService.getProgram(1L)).thenReturn(response);
        //when + then
        mockMvc.perform(get("/programs/{programId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Gold Program"))
                .andExpect(jsonPath("$.description").value("desc"));
        Mockito.verify(programService).getProgram(idCaptor.capture());
        Assertions.assertEquals(1L, idCaptor.getValue());
    }

    @Test
    void getProgram_notExists_returnsNotFoundWithMessage() throws Exception {
        //given
        when(programService.getProgram(999L)).thenThrow(new ProgramNotFoundException(999L));
        //when + then
        mockMvc.perform(get("/programs/{programId}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("PROGRAM_NOT_FOUND"))
                .andExpect(jsonPath("$.message", Matchers.containsString("Program not found: id=999")));
    }

    @Test
    void updateProgram_dataCorrect_returnsUpdatedProgram() throws Exception {
        //given
        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        UpdateProgramRequest command = new UpdateProgramRequest("Platinum Program", "new desc");
        ProgramResponse response = new ProgramResponse(1L, "Platinum Program", "new desc", startDate, endDate);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<UpdateProgramRequest> commandCaptor = ArgumentCaptor.forClass(UpdateProgramRequest.class);
        when(programService.updateProgram(eq(1L), any(UpdateProgramRequest.class))).thenReturn(response);
        //when + then
        mockMvc.perform(put("/programs/{programId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Platinum Program"))
                .andExpect(jsonPath("$.description").value("new desc"));
        Mockito.verify(programService).updateProgram(idCaptor.capture(), commandCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, idCaptor.getValue()),
                () -> Assertions.assertEquals("Platinum Program", commandCaptor.getValue().name()),
                () -> Assertions.assertEquals("new desc", commandCaptor.getValue().description())
        );
    }

    @Test
    void updateProgram_notExists_returnsNotFoundWithMessage() throws Exception {
        //given
        UpdateProgramRequest command = new UpdateProgramRequest("Platinum Program", "desc");
        when(programService.updateProgram(eq(999L), any(UpdateProgramRequest.class)))
                .thenThrow(new ProgramNotFoundException(999L));
        //when + then
        mockMvc.perform(put("/programs/{programId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("PROGRAM_NOT_FOUND"))
                .andExpect(jsonPath("$.message", Matchers.containsString("Program not found: id=999")));
    }

    @Test
    void deleteProgram_exists_returnsNoContent() throws Exception {
        //given
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        //when + then
        mockMvc.perform(delete("/programs/{programId}", 1L))
                .andExpect(status().isNoContent());
        Mockito.verify(programService).deleteProgram(idCaptor.capture());
        Assertions.assertEquals(1L, idCaptor.getValue());
    }

    @Test
    void deleteProgram_notExists_returnsNotFoundWithMessage() throws Exception {
        //given
        Mockito.doThrow(new ProgramNotFoundException(1L)).when(programService).deleteProgram(1L);
        //when + then
        mockMvc.perform(delete("/programs/{programId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("PROGRAM_NOT_FOUND"))
                .andExpect(jsonPath("$.message", Matchers.containsString("Program not found: id=1")));
    }
}