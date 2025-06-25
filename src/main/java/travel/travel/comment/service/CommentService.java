package travel.travel.comment.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import travel.travel.comment.domain.Comment;
import travel.travel.comment.dto.CommentCreateReqDto;
import travel.travel.comment.dto.CommentResDto;
import travel.travel.comment.dto.CommentUpdateReqDto;
import travel.travel.comment.repository.CommentRepository;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.repository.PlanRepository;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    public CommentResDto commentCreate(CommentCreateReqDto commentCreateReqDto) {
        //        String memberId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String memberId = "1";
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

//        Plan plan = planRepository.findById(commentCreateReqDto.getPlanId())
//                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
        Plan plan = null;

        Comment parent = null;
        if (commentCreateReqDto.getParentId() != null) {
            parent = commentRepository.findById(commentCreateReqDto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));
        }

        Comment savedComment = commentRepository.save(commentCreateReqDto.toEntity(member, parent, plan));
        return CommentResDto.fromEntity(savedComment);
    }

    public CommentResDto commentUpdate(Long commentId, CommentUpdateReqDto commentUpdateReqDto) {
        //        String memberId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String memberId = "1";
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Comment findComment = commentRepository.findById(commentId)
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        if (!member.equals(findComment.getMember())) {
            throw new IllegalStateException("본인 댓글만 수정할 수 있습니다.");
        }

        findComment.updateComment(commentUpdateReqDto.getContent());
        return CommentResDto.fromEntity(findComment);
    }

    public CommentResDto commentDelete(Long commentId) {
        //        String memberId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String memberId = "1";
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        if (!member.equals(findComment.getMember())) {
            throw new IllegalStateException("본인 댓글만 수정할 수 있습니다.");
        }

        commentRepository.delete(findComment);
        return CommentResDto.fromEntity(findComment);
    }

    public List<CommentResDto> getCommentsByPost(Long planId) {
        Plan findPlan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
        List<Comment> topLevelComments = commentRepository.findByPlanAndParentIsNull(findPlan);
        return topLevelComments.stream()
                .map(CommentResDto::fromEntity)
                .collect(Collectors.toList());
    }
}
