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
import com.example.loyaltyprogram.validation.Validate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        log.debug("Attempting to create user with email={}", request.email());
        Validate.notNull(request, "request");
        Validate.email(request.email());
        Validate.notBlank(request.firstName(), "firstName");
        Validate.notBlank(request.lastName(), "lastName");

        if (userRepository.existsByEmail(request.email())) {
            log.warn("Cannot create user. Email already in use: {}", request.email());
            throw new ConflictException("EMAIL_ALREADY_EXISTS", "Email already in use: " + request.email());
        }

        User user = userMapper.toEntity(request);

        if (request.programId() != null) {
            LoyaltyProgram program = findLoyaltyProgramById(request.programId());
            LocalDateTime now = LocalDateTime.now();
            if (!program.isActiveAt(now)) {
                log.warn("Cannot assign user to programId={}. Program is expired or inactive", request.programId());
                throw new ProgramExpiredException(program.getId());
            }
            Membership membership = new Membership();
            user.addMembership(membership);
            program.addMembership(membership);
        }

        try {
            User saved = userRepository.save(user);
            log.info("Created user id={} email={}", saved.getId(), saved.getEmail());
            return userMapper.toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Data integrity violation on user creation for email={}", request.email());
            throw new ConflictException("EMAIL_ALREADY_EXISTS", "Email already in use: " + request.email());
        }
    }

    @Transactional(readOnly = true)
    public List<BalanceResponse> getUserPrograms(Long userId) {
        log.debug("Fetching loyalty programs for userId={}", userId);
        findUserById(userId);
        List<BalanceResponse> balances = membershipRepository.findByUserId(userId).stream().map(membershipMapper::toBalanceResponse).toList();
        log.debug("Found {} active program memberships for userId={}", balances.size(), userId);
        return balances;
    }

    @Transactional(readOnly = true)
    public PageDto<UserResponse> searchUsers(String email, String lastName, PageRequestDto pageRequest) {
        log.debug("Searching users with filters: email={}, lastName={}", email, lastName);
        Pageable pageable = pageRequestMapper.toPageable(pageRequest);
        Page<UserResponse> page = userRepository.findByEmailContainingIgnoreCaseAndLastNameContainingIgnoreCase(email == null ? "" : email, lastName == null ? "" : lastName, pageable).map(userMapper::toResponse);
        log.debug("Found {} users matching search criteria", page.getTotalElements());
        return PageDto.from(page);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        log.debug("Fetching details for userId={}", id);
        return userMapper.toResponse(findUserById(id));
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        log.debug("Attempting to update userId={}", id);
        Validate.notNull(request, "request");
        Validate.notBlank(request.firstName(), "firstName");
        Validate.notBlank(request.lastName(), "lastName");
        User user = findUserById(id);
        User updated = user.update(request);
        log.info("User updated successfully for userId={}", id);
        return userMapper.toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Attempting to deactivate userId={}", id);
        User user = findUserById(id);
        user.deactivate();
        log.info("Deactivated user id={}", id);
    }

    @Transactional
    public BalanceResponse joinProgram(Long userId, Long programId) {
        log.debug("Attempting to join user id={} to program id={}", userId, programId);
        User user = findUserById(userId);

        if (user.isDeactivated()) {
            log.warn("Cannot join program. User id={} is deactivated", user.getId());
            throw new ConflictException("USER_DEACTIVATED", "User id=" + user.getId() + " is deactivated");
        }

        LoyaltyProgram program = findLoyaltyProgramById(programId);

        if (membershipRepository.existsByUserIdAndProgramId(userId, programId)) {
            log.warn("User id={} is already a member of program id={}", userId, programId);
            throw new MembershipAlreadyExistsException(userId, programId);
        }

        if (!program.isActiveAt(LocalDateTime.now())) {
            log.warn("Cannot join program. Program id={} is inactive or expired", programId);
            throw new ProgramExpiredException(programId);
        }

        Membership membership = new Membership();
        user.addMembership(membership);
        program.addMembership(membership);
        Membership saved = membershipRepository.save(membership);
        log.info("User id={} successfully joined program id={}", userId, programId);
        return membershipMapper.toBalanceResponse(saved);
    }

    @Transactional
    public void leaveProgram(Long userId, Long programId) {
        log.debug("Attempting to remove user id={} from program id={}", userId, programId);
        Membership membership = membershipRepository.findByUserIdAndProgramId(userId, programId).orElseThrow(() -> {
            log.warn("Membership not found for userId={} and programId={}", userId, programId);
            return new MembershipNotFoundException(userId, programId);
        });

        if (membership.getPointsBalance() != 0) {
            log.warn("Cannot remove membership for userId={} in programId={}. Points balance is non-zero ({})", userId, programId, membership.getPointsBalance());
            throw new ConflictException("MEMBERSHIP_HAS_BALANCE", "Cannot remove membership with non-zero points balance (" + membership.getPointsBalance() + " points would be lost)");
        }

        User user = membership.getUser();
        if (user != null && user.getMemberships() != null) {
            user.getMemberships().remove(membership);
        }

        LoyaltyProgram program = membership.getProgram();
        if (program != null && program.getMemberships() != null) {
            program.getMemberships().remove(membership);
        }

        membershipRepository.delete(membership);
        log.info("User id={} successfully left program id={}", userId, programId);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.warn("User not found for id={}", id);
            return new UserNotFoundException(id);
        });
    }

    private LoyaltyProgram findLoyaltyProgramById(Long id) {
        return programRepository.findById(id).orElseThrow(() -> {
            log.warn("Loyalty program not found for id={}", id);
            return new ProgramNotFoundException(id);
        });
    }
}