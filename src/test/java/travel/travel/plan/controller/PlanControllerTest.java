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
import travel.travel.common.service.AuthService;
import travel.travel.plan.dto.PlanCreateReqDto;
import travel.travel.plan.dto.PlanResDto;
import travel.travel.plan.service.PlanService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private PlanResDto planResDto;

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

}
