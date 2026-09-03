package com.example.loyaltyprogram.service;

import com.example.loyaltyprogram.dto.PageDto;
import com.example.loyaltyprogram.dto.request.CreateProgramRequest;
import com.example.loyaltyprogram.dto.request.PageRequestDto;
import com.example.loyaltyprogram.dto.request.UpdateProgramRequest;
import com.example.loyaltyprogram.dto.response.ProgramResponse;
import com.example.loyaltyprogram.exception.ConflictException;
import com.example.loyaltyprogram.exception.ProgramNotFoundException;
import com.example.loyaltyprogram.mapper.LoyaltyProgramMapper;
import com.example.loyaltyprogram.mapper.PageRequestMapper;
import com.example.loyaltyprogram.model.LoyaltyProgram;
import com.example.loyaltyprogram.repository.LoyaltyProgramRepository;
import com.example.loyaltyprogram.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {

    private final LoyaltyProgramRepository programRepository;
    private final MembershipRepository membershipRepository;
    private final LoyaltyProgramMapper programMapper;
    private final PageRequestMapper pageRequestMapper;

    @Transactional
    public ProgramResponse createProgram(CreateProgramRequest request) {
        if (programRepository.existsByName(request.name())) {
            throw new ConflictException("PROGRAM_NAME_TAKEN", "Program name already in use: " + request.name());
        }
        LoyaltyProgram program = programMapper.toEntity(request);
        LoyaltyProgram saved = programRepository.save(program);
        log.info("Created program id={} name={}", saved.getId(), saved.getName());
        return programMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageDto<ProgramResponse> listPrograms(Boolean active, boolean includeExpired, PageRequestDto pageRequest) {
        Pageable pageable = pageRequestMapper.toPageable(pageRequest);
        LocalDateTime now = LocalDateTime.now();
        Page<LoyaltyProgram> page;
        if (Boolean.TRUE.equals(active)) {
            page = programRepository.findActiveAt(now, pageable);
        } else if (includeExpired) {
            page = programRepository.findAll(pageable);
        } else {
            page = programRepository.findNotExpired(now, pageable);
        }
        return PageDto.from(page.map(programMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public ProgramResponse getProgram(Long programId) {
        return programMapper.toResponse(findProgramById(programId));
    }

    @Transactional
    public ProgramResponse updateProgram(Long programId, UpdateProgramRequest request) {
        LoyaltyProgram program = findProgramById(programId);

        if (!program.getName().equals(request.name()) && programRepository.existsByName(request.name())) {
            throw new ConflictException("PROGRAM_NAME_TAKEN", "Program name already in use: " + request.name());
        }

        return programMapper.toResponse(program.update(request));
    }

    @Transactional
    public void deleteProgram(Long programId) {
        LoyaltyProgram program = findProgramById(programId);

        if (!membershipRepository.findByProgramId(programId).isEmpty()) {
            throw new ConflictException("PROGRAM_HAS_MEMBERSHIPS", "Cannot delete program with active memberships");
        }
        programRepository.delete(program);
        log.info("Deleted program id = {}", programId);
    }

    private LoyaltyProgram findProgramById(Long programId) {
        return programRepository.findById(programId)
                .orElseThrow(() -> new ProgramNotFoundException(programId));
    }

}
