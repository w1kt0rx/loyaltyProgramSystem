package com.example.loyaltyprogram.mapper;

import com.example.loyaltyprogram.dto.request.PageRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageRequestMapperTest {
    private PageRequestMapper pageRequestMapper;

    @BeforeEach
    void setup() {
        this.pageRequestMapper = Mappers.getMapper(PageRequestMapper.class);
    }

    @Test
    void toPageable_fullDto_mapsToPageableWithProvidedValues() {
        // given
        PageRequestDto dto = new PageRequestDto(2, 50, "createdDate,desc");
        // when
        Pageable pageable = pageRequestMapper.toPageable(dto);
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, pageable.getPageNumber()),
                () -> Assertions.assertEquals(50, pageable.getPageSize()),
                () -> Assertions.assertEquals(Sort.by(Sort.Direction.DESC, "createdDate"), pageable.getSort())
        );
    }

    @Test
    void toPageable_nullFieldsInDto_usesDefaultValues() {
        // given
        PageRequestDto dto = new PageRequestDto(null, null, null);
        // when
        Pageable pageable = pageRequestMapper.toPageable(dto);
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(PageRequestDto.DEFAULT_PAGE, pageable.getPageNumber()),
                () -> Assertions.assertEquals(PageRequestDto.DEFAULT_SIZE, pageable.getPageSize()),
                () -> Assertions.assertEquals(Sort.by(Sort.Direction.ASC, PageRequestDto.DEFAULT_SORT_BY), pageable.getSort())
        );
    }

    @Test
    void toPageable_ascDirectionExplicitly_mapsToAscendingSort() {
        // given
        PageRequestDto dto = new PageRequestDto(0, 10, "name,asc");
        // when
        Pageable pageable = pageRequestMapper.toPageable(dto);
        // then
        Assertions.assertEquals(Sort.by(Sort.Direction.ASC, "name"), pageable.getSort());
    }

    @Test
    void toPageable_caseInsensitiveDescDirection_mapsToDescendingSort() {
        // given
        PageRequestDto dto = new PageRequestDto(1, 15, "points,DESC");
        // when
        Pageable pageable = pageRequestMapper.toPageable(dto);
        // then
        Assertions.assertEquals(Sort.by(Sort.Direction.DESC, "points"), pageable.getSort());
    }

    @Test
    void toPageable_sortWithoutDirection_defaultsToAscending() {
        // given
        PageRequestDto dto = new PageRequestDto(0, 10, "lastName");
        // when
        Pageable pageable = pageRequestMapper.toPageable(dto);
        // then
        Assertions.assertEquals(Sort.by(Sort.Direction.ASC, "lastName"), pageable.getSort());
    }

    @Test
    void toPageable_negativePageOrZeroSize_fallsBackToDefaults() {
        // given
        PageRequestDto dto = new PageRequestDto(-1, 0, "id,asc");
        // when
        Pageable pageable = pageRequestMapper.toPageable(dto);
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(PageRequestDto.DEFAULT_PAGE, pageable.getPageNumber()),
                () -> Assertions.assertEquals(PageRequestDto.DEFAULT_SIZE, pageable.getPageSize())
        );
    }

    @Test
    void toPageable_sizeAboveMax_isCappedAtMaxSize() {
        // given
        PageRequestDto dto = new PageRequestDto(0, 500, "id,asc");
        // when
        Pageable pageable = pageRequestMapper.toPageable(dto);
        // then
        Assertions.assertEquals(PageRequestDto.MAX_SIZE, pageable.getPageSize());
    }
}