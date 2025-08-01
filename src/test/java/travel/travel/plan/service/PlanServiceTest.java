package travel.travel.plan.service;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import travel.travel.bookmark.repository.BookmarkRepository;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.domain.Destination;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.dto.PlanCreateReqDto;
import travel.travel.plan.dto.PlanResDto;
import travel.travel.plan.dto.PlanUpdateReqDto;
import travel.travel.plan.repository.DestinationRepository;
import travel.travel.plan.repository.PlanRepository;

import java.time.LocalDate;
import java.util.ArrayList;
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

}