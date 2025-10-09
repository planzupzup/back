package travel.travel.comment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import travel.travel.comment.domain.Comment;
import travel.travel.comment.domain.CommentOwnership;
import travel.travel.comment.domain.CommentSortType;
import travel.travel.comment.dto.CommentCreateReqDto;
import travel.travel.comment.dto.CommentResDto;
import travel.travel.comment.dto.CommentUpdateReqDto;
import travel.travel.comment.repository.CommentRepository;
import travel.travel.common.dto.PageApiResDto;
import travel.travel.common.exception.CustomErrorCode;
import travel.travel.common.exception.CustomException;
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
        Plan plan = findPlan(commentCreateReqDto.getPlanId());

        Comment parent = null;
        if (commentCreateReqDto.getParentId() != null) {
            parent = commentRepository.findById(commentCreateReqDto.getParentId())
                    .orElseThrow(() -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND));
            if (parent.getParent() != null) {
                throw new CustomException(CustomErrorCode.MAX_COMMENT_DEPTH_EXCEEDED);
            }
        }

        Comment savedComment = commentRepository.save(CommentCreateReqDto.toEntity(commentCreateReqDto, member, parent, plan));
        boolean isLiked = likeRepository.existsByMemberAndComment(member, savedComment);
        return CommentResDto.of(savedComment, CommentOwnership.MINE, isLiked);
    }

    public PageApiResDto<CommentResDto> getCommentsByPost(Long planId, int page, int size, CommentSortType type) {
        Plan plan = findPlan(planId);

        if (!plan.isPublic()) {
            throw new CustomException(CustomErrorCode.ACCESS_DENIED);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTime"));

        Page<CommentResDto> commentResDto = switch (type) {
            case LATEST -> commentRepository.findByPlanAndParentIsNull(plan, pageable)
                    .map(comment -> CommentResDto.of(comment, CommentOwnership.OTHERS,false));
            case POPULAR -> commentRepository.findCommentsOrderByLikeCountAndParentIsNull(plan, pageable)
                    .map(comment -> CommentResDto.of(comment, CommentOwnership.OTHERS,false));
        };

        return PageApiResDto.of(commentResDto);
    }

    public PageApiResDto<CommentResDto> getCommentsByPost(Long planId, int page, int size, CommentSortType type, Long memberId) {
        Member member = getMember(memberId);
        Plan plan = findPlan(planId);

        if (!plan.isPublic() && !plan.getMember().equals(member)) {
            throw new CustomException(CustomErrorCode.ACCESS_DENIED);
        }

        List<Long> likedIds = likeRepository.findCommentIdsByMember(member);
        Set<Long> likedSet = new HashSet<>(likedIds);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTime"));

        Page<CommentResDto> commentResDto = switch (type) {
            case LATEST -> commentRepository.findByPlanAndParentIsNull(plan, pageable)
                    .map(comment -> {
                        if (comment.getMember() == member)
                            return CommentResDto.of(comment, CommentOwnership.MINE, likedSet.contains(comment.getCommentId()));
                        else
                            return CommentResDto.of(comment, CommentOwnership.OTHERS, likedSet.contains(comment.getCommentId()));
                    });
            case POPULAR -> commentRepository.findCommentsOrderByLikeCountAndParentIsNull(plan, pageable)
                    .map(comment -> {
                        if (comment.getMember() == member)
                            return CommentResDto.of(comment, CommentOwnership.MINE, likedSet.contains(comment.getCommentId()));
                        else
                            return CommentResDto.of(comment, CommentOwnership.OTHERS, likedSet.contains(comment.getCommentId()));
                    });

        };

        return PageApiResDto.of(commentResDto);
    }

    public PageApiResDto<CommentResDto> getCommentsByParent(Long planId, Long commentId, int page, int size, CommentSortType type) {
        Plan plan = findPlan(planId);
        Comment parent = findComment(commentId);

        if (!plan.isPublic()) {
            throw new CustomException(CustomErrorCode.ACCESS_DENIED);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTime"));

        Page<CommentResDto> commentResDto = switch (type) {
            case LATEST -> commentRepository.findByParent(parent, pageable)
                    .map(comment -> CommentResDto.of(comment, CommentOwnership.OTHERS,false));
            case POPULAR -> commentRepository.findCommentsOrderByLikeCountAndParent(parent, pageable)
                    .map(comment -> CommentResDto.of(comment, CommentOwnership.OTHERS,false));
        };


        return PageApiResDto.of(commentResDto);
    }

    public PageApiResDto<CommentResDto> getCommentsByParent(Long planId, Long commentId, int page, int size, CommentSortType type, Long memberId) {
        Member member = getMember(memberId);

        Plan plan = findPlan(planId);
        Comment parent = findComment(commentId);

        if (!plan.isPublic() && !plan.getMember().equals(member)) {
            throw new CustomException(CustomErrorCode.ACCESS_DENIED);
        }

        List<Long> likedIds = likeRepository.findCommentIdsByMember(member);
        Set<Long> likedSet = new HashSet<>(likedIds);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTime"));

        Page<CommentResDto> commentResDto = switch (type) {
            case LATEST -> commentRepository.findByParent(parent, pageable)
                    .map(comment -> {
                        if (comment.getMember() == member)
                            return CommentResDto.of(comment, CommentOwnership.MINE, likedSet.contains(comment.getCommentId()));
                        else
                            return CommentResDto.of(comment, CommentOwnership.OTHERS, likedSet.contains(comment.getCommentId()));
                    });
            case POPULAR -> commentRepository.findCommentsOrderByLikeCountAndParent(parent, pageable)
                    .map(comment -> {
                        if (comment.getMember() == member)
                            return CommentResDto.of(comment, CommentOwnership.MINE, likedSet.contains(comment.getCommentId()));
                        else
                            return CommentResDto.of(comment, CommentOwnership.OTHERS, likedSet.contains(comment.getCommentId()));
                    });
        };

        return PageApiResDto.of(commentResDto);
    }

    public CommentResDto updateComment(Long commentId, CommentUpdateReqDto commentUpdateReqDto, Long memberId) {
        Member member = getMember(memberId);

        Comment findComment = findComment(commentId);

        if (!member.equals(findComment.getMember())) {
            throw new CustomException(CustomErrorCode.UPDATE_DENIED);
        }

        findComment.updateComment(commentUpdateReqDto.getContent());
        boolean isLiked = likeRepository.existsByMemberAndComment(member, findComment);
        return CommentResDto.of(findComment, CommentOwnership.MINE, isLiked);
    }

    public void deleteComment(Long commentId, Long memberId) {
        Member member = getMember(memberId);
        Comment findComment = findComment(commentId);
        if (!member.equals(findComment.getMember())) {
            throw new CustomException(CustomErrorCode.DELETE_DENIED);
        }

        if (findComment.getParent() != null) {
            findComment.getParent().getChildren().remove(findComment);
        }

        commentRepository.delete(findComment);
    }

    private Plan findPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PLAN_NOT_FOUND));
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND));
    }

}