package travel.travel.plan.service;

import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import travel.travel.bookmark.repository.BookmarkRepository;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.domain.Destination;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.dto.PlanCreateReqDto;
import travel.travel.plan.dto.PlanResDto;
import travel.travel.plan.repository.DestinationRepository;
import travel.travel.plan.repository.PlanRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private DestinationRepository destinationRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @InjectMocks
    private PlanService planService;

    private Member testMember;
    private Destination testDestination;
    private Plan testPlan;
    private Plan testPlan2;
    private PlanCreateReqDto createReqDto;


    @BeforeEach
    void setUp() {
        testMember = new Member("kakao123");
        testMember.updateNickName("test-user");

        testDestination = new Destination(1L, "서울");

        testPlan = Plan.builder()
                .planId(1L)
                .title("Test Plan")
                .content("Test Content")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 3))
                .isPublic(true)
                .member(testMember)
                .destination(testDestination)
                .locations(new ArrayList<>())
                .build();

        testPlan2 = Plan.builder()
                .planId(2L)
                .title("Test Plan2")
                .content("Test Content2")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 3))
                .isPublic(true)
                .member(testMember)
                .destination(testDestination)
                .locations(new ArrayList<>())
                .build();

        createReqDto = PlanCreateReqDto.builder()
                .title("Test Plan")
                .content("Test Content")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 3))
                .isPublic(true)
                .destinationName("서울")
                .build();

    }


    @Test
    @DisplayName("계획 생성 성공")
    void createPlan_Success() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(testMember));
        given(destinationRepository.findByDestinationName("서울")).willReturn(Optional.of(testDestination));
        given(planRepository.save(any(Plan.class))).willReturn(testPlan);
        given(bookmarkRepository.existsByMemberAndPlan(testMember, testPlan)).willReturn(false);

        // when
        PlanResDto result = planService.createPlan(createReqDto, 1L);

        // then
        assertThat(result.getTitle()).isEqualTo("Test Plan");
        assertThat(result.getContent()).isEqualTo("Test Content");
        assertThat(result.getIsBookMarked()).isFalse();
        verify(planRepository).save(any(Plan.class));
    }

    @Test
    @DisplayName("계획 생성 실패 - 존재하지 않는 목적지")
    void createPlan_FailByInvalidDestination() {
        // given
        given(memberRepository.findById(1L)).willReturn(Optional.of(testMember));
        given(destinationRepository.findByDestinationName("서울")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> planService.createPlan(createReqDto, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 장소입니다.");
    }

    @Test
    @DisplayName("계획 생성 실패 - 잘못된 날짜")
    void createPlan_FailByInvalidDate() {
        // given
        PlanCreateReqDto invalidDateDto = PlanCreateReqDto.builder()
                .title("Test Plan")
                .content("Test Content")
                .startDate(LocalDate.of(2024, 1, 5))
                .endDate(LocalDate.of(2024, 1, 3))
                .isPublic(true)
                .destinationName("Seoul")
                .build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(testMember));
        given(destinationRepository.findByDestinationName("Seoul")).willReturn(Optional.of(testDestination));

        // when & then
        assertThatThrownBy(() -> planService.createPlan(invalidDateDto, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("시작일은 종료일보다 이전이어야 합니다.");
    }

    @Test
    @DisplayName("계획 조회 성공")
    void getPlan_Success() {
        // given
        given(planRepository.findById(1L)).willReturn(Optional.of(testPlan));
        given(memberRepository.findById(1L)).willReturn(Optional.of(testMember));
        given(bookmarkRepository.existsByMemberAndPlan(testMember, testPlan)).willReturn(true);

        // when
        PlanResDto result = planService.getPlan(1L, 1L);

        // then
        assertThat(result.getTitle()).isEqualTo("Test Plan");
        assertThat(result.getIsBookMarked()).isTrue();
    }

    @Test
    @DisplayName("계획 조회 실패 - 존재하지 않는 계획")
    void getPlan_FailByNotFound() {
        // given
        given(planRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> planService.getPlan(1L, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 계획입니다.");
    }

    @Test
    @DisplayName("공개 계획 목록 조회 성공")
    void getAllPlan_Success() {
        // given
        List<Plan> plans = List.of(testPlan, testPlan2);
        Page<Plan> planPage = new PageImpl<>(plans);

        given(planRepository.findAllByIsPublicTrue(any(Pageable.class))).willReturn(planPage);

        // when
        var result = planService.getAllPlan(0, 10);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Test Plan");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("Test Plan2");

    }




}