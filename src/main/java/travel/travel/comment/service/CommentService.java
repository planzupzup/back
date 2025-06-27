package travel.travel.comment.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public CommentResDto createComment(CommentCreateReqDto commentCreateReqDto) {
        //        String memberId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String memberId = "1";
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Plan plan = planRepository.findById(commentCreateReqDto.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        Comment parent = null;
        if (commentCreateReqDto.getParentId() != null) {
            parent = commentRepository.findById(commentCreateReqDto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));
            if (parent.getParent() != null) {
                throw new IllegalArgumentException("대댓글(2단계)까지만 작성할 수 있습니다.");
            }
        }

        Comment savedComment = commentRepository.save(commentCreateReqDto.toEntity(member, parent, plan));
        return CommentResDto.fromEntity(savedComment);
    }

    public CommentResDto updateComment(Long commentId, CommentUpdateReqDto commentUpdateReqDto) {
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

    public CommentResDto deleteComment(Long commentId) {
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

    public Page<CommentResDto> getCommentsByPost(Long planId, int page, int size) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
        Pageable pageable = PageRequest.of(page, size, Sort.by("commentId").ascending());
        Page<Comment> pageResult = commentRepository.findByPlanAndParentIsNull(plan, pageable);
        return pageResult.map(CommentResDto::fromEntity);
    }

    public List<CommentResDto> getCommentsByParent(Long planId, Long commentId) {
        planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        return findComment.getChildren().stream().map(CommentResDto::fromEntity)
                .collect(Collectors.toList());
    }
}
