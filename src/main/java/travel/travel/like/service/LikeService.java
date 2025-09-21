package travel.travel.like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.travel.comment.domain.Comment;
import travel.travel.common.exception.CustomErrorCode;
import travel.travel.common.exception.CustomException;
import travel.travel.comment.repository.CommentRepository;
import travel.travel.like.domain.Like;
import travel.travel.like.repository.LikeRepository;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.repository.PlanRepository;

import java.util.Optional;


@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    public void addLike(Long commentId, Long memberId) {
        Member member = getMember(memberId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND));

        planRepository.findById(comment.getPlan().getPlanId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.PLAN_NOT_FOUND));

        Optional<Like> existingLike = likeRepository.findByMemberAndComment(member, comment);
        if (existingLike.isEmpty()) {
            Like like = Like.builder()
                    .member(member)
                    .comment(comment)
                    .build();
            likeRepository.save(like);
        } else {
            throw new CustomException(CustomErrorCode.ALREADY_LIKED);
        }
    }

    public void removeLike(Long commentId, Long memberId) {
        Member member = getMember(memberId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND));

        planRepository.findById(comment.getPlan().getPlanId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.PLAN_NOT_FOUND));

        Optional<Like> existingLike = likeRepository.findByMemberAndComment(member, comment);
        existingLike.ifPresent(likeRepository::delete);
    }

    public long getLikeCount(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND));

        planRepository.findById(comment.getPlan().getPlanId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.PLAN_NOT_FOUND));

        return likeRepository.countByComment(comment);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));
    }
}