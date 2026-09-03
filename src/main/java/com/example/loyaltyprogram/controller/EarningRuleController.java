package com.example.loyaltyprogram.controller;

import com.example.loyaltyprogram.dto.request.CreateEarningRuleRequest;
import com.example.loyaltyprogram.dto.request.UpdateEarningRuleRequest;
import com.example.loyaltyprogram.dto.response.EarningRuleResponse;
import com.example.loyaltyprogram.service.EarningRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class EarningRuleController {
    private final EarningRuleService earningRuleService;

    @PostMapping("/programs/{programId}/earning-rules")
    public ResponseEntity<EarningRuleResponse> createEarningRule(@PathVariable Long programId, @RequestBody CreateEarningRuleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(earningRuleService.createRule(programId, request));
    }

    @GetMapping("/programs/{programId}/earning-rules")
    public ResponseEntity<List<EarningRuleResponse>> listRules(@PathVariable Long programId) {
        return ResponseEntity.ok(earningRuleService.listRules(programId));
    }

    @GetMapping("/earning-rule/{earningRuleId}")
    public ResponseEntity<EarningRuleResponse> getEarningRule(@PathVariable Long earningRuleId) {
        return ResponseEntity.ok(earningRuleService.getEarningRule(earningRuleId));
    }

    @PutMapping("/earning-rule/{earningRuleId}")
    public ResponseEntity<EarningRuleResponse> updateEarningRule(@PathVariable Long earningRuleId, @RequestBody UpdateEarningRuleRequest request) {
        return ResponseEntity.ok(earningRuleService.updateEarningRule(earningRuleId, request));
    }

    @DeleteMapping("/earning-rule/{earningRuleId}")
    public ResponseEntity<Void> deleteEarningRule(@PathVariable Long earningRuleId) {
        earningRuleService.delete(earningRuleId);
        return ResponseEntity.noContent().build();
    }
}
