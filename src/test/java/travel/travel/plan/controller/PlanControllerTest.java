package travel.travel.plan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import travel.travel.common.dto.PageApiResponse;
import travel.travel.common.service.AuthService;
import travel.travel.plan.dto.PlanCreateReqDto;
import travel.travel.plan.dto.PlanResDto;
import travel.travel.plan.dto.PlanThumbResDto;
import travel.travel.plan.dto.PlanUpdateReqDto;
import travel.travel.plan.service.PlanService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PlanController.class)
class PlanControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PlanService planService;

    @MockBean
    AuthService authService;

    private PlanCreateReqDto createReqDto;
    private PlanUpdateReqDto updateReqDto;
    private PlanResDto planResDto;
    private PlanResDto updatedPlanResDto;
    private PlanThumbResDto planThumbResDto;
    private PlanThumbResDto planThumbResDto2;

    @BeforeEach
    void setUp() {

        createReqDto = PlanCreateReqDto.builder()
                .title("Test Plan")
                .content("Test Content")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 3))
                .isPublic(true)
                .destinationName("서울")
                .build();

        updateReqDto = PlanUpdateReqDto.builder()
                .title("Updated Plan")
                .content("Updated Content")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 5))
                .isPublic(false)
                .build();


        planResDto = PlanResDto.builder()
                .planId(1L)
                .title("Test Plan")
                .content("Test Content")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 3))
                .isPublic(true)
                .isBookMarked(false)
                .nickName("test-user")
                .destinationName("서울")
                .locations(List.of())
                .build();

        updatedPlanResDto = PlanResDto.builder()
                .planId(1L)
                .title(updateReqDto.getTitle())
                .content(updateReqDto.getContent())
                .build();

        planThumbResDto = PlanThumbResDto.builder()
                .planId(1L)
                .title("Test Plan")
                .isBookMarked(false)
                .nickName("test-user")
                .destinationName("서울")
                .build();

        planThumbResDto2 = PlanThumbResDto.builder()
                .planId(1L)
                .title("Test Plan2")
                .isBookMarked(false)
                .nickName("test-user")
                .destinationName("서울")
                .build();

    }

    @Test
    @WithMockUser
    @DisplayName("계획 생성 성공")
    void createPlan_Success() throws Exception {
        // given
        given(authService.getAuthenticatedUserId()).willReturn(1L);
        given(planService.createPlan(any(PlanCreateReqDto.class), eq(1L))).willReturn(planResDto);

        // when & then
        mockMvc.perform(post("/api/plan")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReqDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.statusMessage").value("계획생성이 성공적으로 되었습니다."))
                .andExpect(jsonPath("$.result.title").value("Test Plan"))
                .andExpect(jsonPath("$.result.content").value("Test Content"))
                .andExpect(jsonPath("$.result.isBookMarked").value(false));

    }

    @Test
    @WithMockUser
    @DisplayName("계획 생성 실패 - 유효성 검증 오류")
    void createPlan_FailByValidation() throws Exception {
        // given
        PlanCreateReqDto invalidDto = PlanCreateReqDto.builder()
                .title("")
                .content("Test Content")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 3))
                .isPublic(true)
                .destinationName("Seoul")
                .build();

        // when & then
        mockMvc.perform(post("/api/plan")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("계획 상세 조회 성공 - 인증된 사용자")
    void getPlan_Success_AuthenticatedUser() throws Exception {
        // given
        given(authService.isAuthenticatedUser()).willReturn(true);
        given(authService.getAuthenticatedUserId()).willReturn(1L);
        given(planService.getPlan(1L, 1L)).willReturn(planResDto);

        // when & then
        mockMvc.perform(get("/api/plan/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.statusMessage").value("계획상세조회가 성공적으로 되었습니다."))
                .andExpect(jsonPath("$.result.title").value("Test Plan"))
                .andExpect(jsonPath("$.result.content").value("Test Content"))
                .andExpect(jsonPath("$.result.isBookMarked").value(false));

    }

    @Test
    @WithMockUser
    @DisplayName("계획 상세 조회 성공 - 비인증 사용자")
    void getPlan_Success_UnauthenticatedUser() throws Exception {
        // given
        given(authService.isAuthenticatedUser()).willReturn(false);
        given(planService.getPlan(1L)).willReturn(planResDto);

        // when & then
        mockMvc.perform(get("/api/plan/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.statusMessage").value("계획상세조회가 성공적으로 되었습니다."))
                .andExpect(jsonPath("$.result.title").value("Test Plan"))
                .andExpect(jsonPath("$.result.content").value("Test Content"))
                .andExpect(jsonPath("$.result.isBookMarked").value(false));
    }

    @Test
    @WithMockUser
    @DisplayName("계획 목록 조회 성공")
    void getAllPlan_Success() throws Exception {
        // given
        PageApiResponse<PlanThumbResDto> pageResponse = PageApiResponse.of(
                new org.springframework.data.domain.PageImpl<>(List.of(planThumbResDto))
        );
        given(authService.isAuthenticatedUser()).willReturn(false);
        given(planService.getAllPlan(0, 10)).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/api/plan")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.statusMessage").value("계획목록조회가 성공적으로 되었습니다."))
                .andExpect(jsonPath("$.result.content[0].title").value("Test Plan"));
    }

    @Test
    @WithMockUser
    @DisplayName("계획 검색 성공")
    void getAllPlanByKeyword_Success() throws Exception {
        // given
        PageApiResponse<PlanThumbResDto> pageResponse = PageApiResponse.of(
                new org.springframework.data.domain.PageImpl<>(List.of(planThumbResDto, planThumbResDto2))
        );
        given(authService.isAuthenticatedUser()).willReturn(false);
        given(planService.getAllPlanByKeyword("Test", 0, 10)).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/api/plan/search/Test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.statusMessage").value("계획목록조회가 성공적으로 되었습니다."))
                .andExpect(jsonPath("$.result.content[0].title").value("Test Plan"))
                .andExpect(jsonPath("$.result.content.length()").value(2));
    }

    @Test
    @WithMockUser
    @DisplayName("계획 검색 성공")
    void getAllPlanByKeyword_Success_V2() throws Exception {
        // given
        PageApiResponse<PlanThumbResDto> pageResponse = PageApiResponse.of(
                new org.springframework.data.domain.PageImpl<>(List.of(planThumbResDto2))
        );
        given(authService.isAuthenticatedUser()).willReturn(false);
        given(planService.getAllPlanByKeyword("2", 0, 10)).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/api/plan/search/2")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.statusMessage").value("계획목록조회가 성공적으로 되었습니다."))
                .andExpect(jsonPath("$.result.content[0].title").value("Test Plan2"))
                .andExpect(jsonPath("$.result.content.length()").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("계획 수정 성공")
    void updatePlan_Success() throws Exception {
        // given
        given(authService.getAuthenticatedUserId()).willReturn(1L);
        given(planService.updatePlan(eq(1L), any(PlanUpdateReqDto.class), eq(1L))).willReturn(updatedPlanResDto);

        // when & then
        mockMvc.perform(put("/api/plan/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReqDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.statusMessage").value("계획수정이 성공적으로 되었습니다."))
                .andExpect(jsonPath("$.result.title").value("Updated Plan"));
    }

}
