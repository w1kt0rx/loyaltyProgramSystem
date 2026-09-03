package com.example.loyaltyprogram.controller;

import com.example.loyaltyprogram.dto.PageDto;
import com.example.loyaltyprogram.dto.request.CreateProgramRequest;
import com.example.loyaltyprogram.dto.request.PageRequestDto;
import com.example.loyaltyprogram.dto.request.UpdateProgramRequest;
import com.example.loyaltyprogram.dto.response.ProgramResponse;
import com.example.loyaltyprogram.service.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    @PostMapping
    public ResponseEntity<ProgramResponse> createProgram(@RequestBody CreateProgramRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(programService.createProgram(request));
    }

    @GetMapping
    public ResponseEntity<PageDto<ProgramResponse>> getPrograms
            (@RequestParam(required = false) Boolean active,
             @RequestParam(defaultValue = "false") boolean includeExpired,
             PageRequestDto pageRequest) {
        return ResponseEntity.ok(programService.listPrograms(active, includeExpired, pageRequest));
    }

    @GetMapping("/{programId}")
    public ResponseEntity<ProgramResponse> getProgram(@PathVariable Long programId) {
        return ResponseEntity.ok(programService.getProgram(programId));
    }

    @PutMapping("/{programId}")
    public ResponseEntity<ProgramResponse> updateProgram(@PathVariable Long programId, @RequestBody UpdateProgramRequest request) {
        return ResponseEntity.ok(programService.updateProgram(programId, request));
    }

    @DeleteMapping("/{programId}")
    public ResponseEntity<Void> deleteProgram(@PathVariable Long programId) {
        programService.deleteProgram(programId);
        return ResponseEntity.noContent().build();
    }
}
