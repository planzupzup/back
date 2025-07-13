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

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final LikeRepository likeRepository;

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
        boolean isLiked = likeRepository.existsByMemberAndComment(member, savedComment);
        return CommentResDto.of(savedComment,isLiked);
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
        boolean isLiked = likeRepository.existsByMemberAndComment(member, findComment);
        return CommentResDto.of(findComment, isLiked);
    }

    public CommentResDto deleteComment(Long commentId) {
        //        String memberId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String memberId = "1";
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        boolean isLiked = likeRepository.existsByMemberAndComment(member, findComment);

        if (!member.equals(findComment.getMember())) {
            throw new IllegalStateException("본인 댓글만 수정할 수 있습니다.");
        }

        if (findComment.getParent() != null) {
            findComment.getParent().getChildren().size();
            findComment.getParent().getChildren().remove(findComment);
        }

        commentRepository.delete(findComment);
        return CommentResDto.of(findComment, isLiked);
    }

    public PageApiResponse<CommentResDto> getCommentsByPost(Long planId, int page, int size) {
        String memberId = "1";
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdTime"));
        Page<Comment> comments = commentRepository.findByPlanAndParentIsNull(plan, pageable);


        List<Long> likedIds = likeRepository.findCommentIdsByMember(member);
        Set<Long> likedSet = new HashSet<>(likedIds);

        List<CommentResDto> content = comments.stream()
                .map(comment -> CommentResDto.of(comment, likedSet.contains(comment.getCommentId())))
                .toList();

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
        String memberId = "1";
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
        Comment parent = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdTime"));

        Page<Comment> comments = commentRepository.findByParent(parent, pageable);

        List<Long> likedIds = likeRepository.findCommentIdsByMember(member);
        Set<Long> likedSet = new HashSet<>(likedIds);

        List<CommentResDto> content = comments.stream()
                .map(comment -> CommentResDto.of(comment, likedSet.contains(comment.getCommentId())))
                .toList();


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