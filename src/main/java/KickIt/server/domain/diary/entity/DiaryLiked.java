package KickIt.server.domain.diary.entity;

import KickIt.server.domain.member.entity.Member;
import KickIt.server.global.util.BaseEntity;
import KickIt.server.global.util.CreatedAt;
import jakarta.persistence.*;
import lombok.*;
import org.joda.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryLiked extends CreatedAt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

}
