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
import travel.travel.like.repository.LikeRepository;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.repository.PlanRepository;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final LikeRepository likeRepository;

    public CommentResDto createComment(CommentCreateReqDto commentCreateReqDto, Long memberId) {
        Member member = getMember(memberId);

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
        boolean isLiked = likeRepository.existsByMemberAndComment(member, savedComment);
        return CommentResDto.of(savedComment,isLiked);
    }

    public PageApiResponse<CommentResDto> getCommentsByPost(Long planId, int page, int size) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdTime"));
        Page<CommentResDto> commentResDto = commentRepository.findByPlanAndParentIsNull(plan, pageable)
                .map(comment -> CommentResDto.of(comment, false));

        return PageApiResponse.of(commentResDto);
    }

    public PageApiResponse<CommentResDto> getCommentsByPost(Long planId, int page, int size, Long memberId) {
        Member member = getMember(memberId);

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        List<Long> likedIds = likeRepository.findCommentIdsByMember(member);
        Set<Long> likedSet = new HashSet<>(likedIds);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdTime"));
        Page<CommentResDto> commentResDto = commentRepository.findByPlanAndParentIsNull(plan, pageable)
                .map(comment -> CommentResDto.of(comment, likedSet.contains(comment.getCommentId())));

        return PageApiResponse.of(commentResDto);
    }

    public PageApiResponse<CommentResDto> getCommentsByParent(Long planId, Long commentId, int page, int size) {

        planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
        Comment parent = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTime"));
        Page<CommentResDto> commentResDto = commentRepository.findByParent(parent, pageable)
                .map(comment -> CommentResDto.of(comment, false));

        return PageApiResponse.of(commentResDto);
    }

    public PageApiResponse<CommentResDto> getCommentsByParent(Long planId, Long commentId, int page, int size, Long memberId) {
        Member member = getMember(memberId);

        planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
        Comment parent = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        List<Long> likedIds = likeRepository.findCommentIdsByMember(member);
        Set<Long> likedSet = new HashSet<>(likedIds);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTime"));
        Page<CommentResDto> commentResDto = commentRepository.findByParent(parent, pageable)
                .map(comment -> CommentResDto.of(comment, likedSet.contains(comment.getCommentId())));

        return PageApiResponse.of(commentResDto);
    }

    public CommentResDto updateComment(Long commentId, CommentUpdateReqDto commentUpdateReqDto, Long memberId) {
        Member member = getMember(memberId);

        Comment findComment = commentRepository.findById(commentId)
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        if (!member.equals(findComment.getMember())) {
            throw new IllegalStateException("본인 댓글만 수정할 수 있습니다.");
        }

        findComment.updateComment(commentUpdateReqDto.getContent());
        boolean isLiked = likeRepository.existsByMemberAndComment(member, findComment);
        return CommentResDto.of(findComment, isLiked);
    }

    public void deleteComment(Long commentId, Long memberId) {
        Member member = getMember(memberId);

        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));
        if (!member.equals(findComment.getMember())) {
            throw new IllegalStateException("본인 댓글만 삭제할 수 있습니다.");
        }

        if (findComment.getParent() != null) {
            findComment.getParent().getChildren().remove(findComment);
        }

        commentRepository.delete(findComment);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}