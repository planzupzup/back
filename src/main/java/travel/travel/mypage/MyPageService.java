package travel.travel.mypage;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;

@Transactional
@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;

    public String updateNickName(NickNameReqDto nickNameReqDto) {
        Member member = getMember();
        member.updateNickName(nickNameReqDto.getNickName());
        return nickNameReqDto.getNickName();
    }

    private Member getMember() {
        //        String memberId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String memberId = "1";
        return memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}
