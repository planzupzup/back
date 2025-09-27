package travel.travel.like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.travel.common.exception.CustomErrorCode;
import travel.travel.common.exception.CustomException;
import travel.travel.comment.repository.CommentRepository;
import travel.travel.like.repository.LikeRepository;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.repository.PlanRepository;


@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    @Transactional
    public void addLike(Long commentId, Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new CustomException(CustomErrorCode.MEMBER_NOT_FOUND);
        }

        if (!commentRepository.existsById(commentId)) {
            throw new CustomException(CustomErrorCode.COMMENT_NOT_FOUND);
        }

        likeRepository.insertLikeIfNotExists(memberId, commentId);
    }

    @Transactional
    public void removeLike(Long commentId, Long memberId) {
        likeRepository.deleteByCommentIdAndMemberId(commentId, memberId);
    }

    public long getLikeCount(Long commentId) {
        return likeRepository.countLikesByCommentId(commentId);
    }

}