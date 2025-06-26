package travel.travel.bookmark.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import travel.travel.member.domain.Member;
import travel.travel.plan.domain.Plan;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bookmark")
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkId;

    @ManyToOne
    private Member member;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;
}
