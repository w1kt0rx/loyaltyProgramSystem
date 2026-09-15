package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.request.PageRequestDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Mapper(config = LoyaltyMapperConfig.class)
public interface PageRequestMapper {

    default Pageable toPageable(PageRequestDto dto) {
        int page = (dto.page() != null && dto.page() >= 0) ? dto.page() : PageRequestDto.DEFAULT_PAGE;
        int size = (dto.size() != null && dto.size() > 0)
                ? Math.min(dto.size(), PageRequestDto.MAX_SIZE)
                : PageRequestDto.DEFAULT_SIZE;
        return PageRequest.of(page, size, toSort(dto.sort()));
    }

    private Sort toSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, PageRequestDto.DEFAULT_SORT_BY);
        }
        String[] parts = sort.split(",", 2);
        String property = parts[0].trim();
        if (property.isEmpty()) {
            return Sort.by(Sort.Direction.ASC, PageRequestDto.DEFAULT_SORT_BY);
        }
        Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(direction, property);
    }
}