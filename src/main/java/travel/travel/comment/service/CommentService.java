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
import travel.travel.common.dto.PageApiResponse;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.repository.PlanRepository;

import java.util.List;


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

        Comment savedComment = commentRepository.save(CommentCreateReqDto.toEntity(commentCreateReqDto, member, parent, plan));
        return CommentResDto.of(savedComment);
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
        return CommentResDto.of(findComment);
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

        if (findComment.getParent() != null) {
            findComment.getParent().getChildren().remove(findComment);
        }

        commentRepository.delete(findComment);
        return CommentResDto.of(findComment);
    }

    public PageApiResponse<CommentResDto> getCommentsByPost(Long planId, int page, int size) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdTime"));
        Page<Comment> comments = commentRepository.findByPlanAndParentIsNull(plan, pageable);
        List<CommentResDto> content = comments.map(CommentResDto::of).getContent();
        return PageApiResponse .<CommentResDto>builder()
                .content(content)
                .page(comments.getNumber())
                .size(comments.getSize())
                .totalPages(comments.getTotalPages())
                .totalElements(comments.getTotalElements())
                .first(comments.isFirst())
                .last(comments.isLast())
                .build();
    }

    public PageApiResponse<CommentResDto> getCommentsByParent(Long planId, Long commentId, int page, int size) {
        planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
        Comment parent = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdTime"));

        Page<Comment> comments = commentRepository.findByParent(parent, pageable);
        List<CommentResDto> content = comments.map(CommentResDto::of).getContent();

        return PageApiResponse .<CommentResDto>builder()
                .content(content)
                .page(comments.getNumber())
                .size(comments.getSize())
                .totalPages(comments.getTotalPages())
                .totalElements(comments.getTotalElements())
                .first(comments.isFirst())
                .last(comments.isLast())
                .build();
    }
}