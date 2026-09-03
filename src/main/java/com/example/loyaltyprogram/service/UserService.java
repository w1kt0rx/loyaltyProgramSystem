package com.example.loyaltyprogram.service;

import com.example.loyaltyprogram.dto.PageDto;
import com.example.loyaltyprogram.dto.request.CreateUserRequest;
import com.example.loyaltyprogram.dto.request.PageRequestDto;
import com.example.loyaltyprogram.dto.request.UpdateUserRequest;
import com.example.loyaltyprogram.dto.response.BalanceResponse;
import com.example.loyaltyprogram.dto.response.UserResponse;
import com.example.loyaltyprogram.exception.*;
import com.example.loyaltyprogram.mapper.MembershipMapper;
import com.example.loyaltyprogram.mapper.PageRequestMapper;
import com.example.loyaltyprogram.mapper.UserMapper;
import com.example.loyaltyprogram.model.LoyaltyProgram;
import com.example.loyaltyprogram.model.Membership;
import com.example.loyaltyprogram.model.User;
import com.example.loyaltyprogram.repository.LoyaltyProgramRepository;
import com.example.loyaltyprogram.repository.MembershipRepository;
import com.example.loyaltyprogram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MembershipMapper membershipMapper;
    private final PageRequestMapper pageRequestMapper;
    private final LoyaltyProgramRepository programRepository;
    private final MembershipRepository membershipRepository;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("EMAIL_ALREADY_EXISTS", "Email already in use: " + request.email());
        }
        User user = userMapper.toEntity(request);

        if (request.programId() != null) {
            LoyaltyProgram program = findLoyaltyProgramById(request.programId());
            LocalDateTime now = LocalDateTime.now();
            if (!program.isActiveAt(now)) {
                throw new ProgramExpiredException(program.getId());
            }
            Membership membership = new Membership();
            user.addMembership(membership);
            program.addMembership(membership);
            membershipRepository.save(membership);
        }

        try {
            User saved = userRepository.save(user);
            log.info("Created user id={} email={}", saved.getId(), saved.getEmail());
            return userMapper.toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("EMAIL_ALREADY_EXISTS", "Email already in use: " + request.email());
        }
    }

    @Transactional(readOnly = true)
    public PageDto<UserResponse> searchUsers(
            String email,
            String lastName,
            PageRequestDto pageRequest
    ) {
        Pageable pageable = pageRequestMapper.toPageable(pageRequest);

        Page<UserResponse> page =
                userRepository
                        .findByEmailContainingIgnoreCaseAndLastNameContainingIgnoreCase(
                                email == null ? "" : email,
                                lastName == null ? "" : lastName,
                                pageable
                        )
                        .map(userMapper::toResponse);

        return PageDto.from(page);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return userMapper.toResponse(findUserById(id));
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = findUserById(id);
        return userMapper.toResponse(user.update(request));
    }

    @Transactional
    public void delete(Long id) {
        User user = findUserById(id);
        user.deactivate();
        log.info("Deactivated user id={}", id);
    }

    @Transactional
    public BalanceResponse joinProgram(Long userId, Long programId) {
        User user = findUserById(userId);
        LoyaltyProgram program = findLoyaltyProgramById(programId);
        if (membershipRepository.existsByUserIdAndProgramId(userId, programId)) {
            throw new MembershipAlreadyExistsException(userId, programId);
        }
        if (!program.isActiveAt(LocalDateTime.now())) {
            throw new ProgramExpiredException(programId);
        }
        Membership membership = new Membership();
        user.addMembership(membership);
        program.addMembership(membership);

        return membershipMapper.toBalanceResponse(membershipRepository.save(membership));
    }

    @Transactional
    public void leaveProgram(Long userId, Long programId) {
        Membership membership = membershipRepository.findByUserIdAndProgramId(userId, programId)
                .orElseThrow(() -> new MembershipNotFoundException(userId, programId));

        if (membership.getPointsBalance() != 0) {
            throw new ConflictException("MEMBERSHIP_HAS_BALANCE",
                    "Cannot remove membership with non-zero points balance ("
                            + membership.getPointsBalance() + " points would be lost)");
        }

        membershipRepository.delete(membership);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private LoyaltyProgram findLoyaltyProgramById(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new ProgramNotFoundException(id));
    }
}
