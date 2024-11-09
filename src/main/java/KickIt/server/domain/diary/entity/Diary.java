package KickIt.server.domain.diary.entity;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.global.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixture_id", nullable = false)
    private Fixture fixture;
    
    private String teamName;

    private int emotion;

    @Column(nullable = true, length = 500)
    private String diaryContent;
    private int likeCount;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DiaryPhoto> diaryPhotos = new ArrayList<>();

    private String mom;
    private boolean isPublic;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiaryLiked> likedBy;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiaryReport> diaryReports = new ArrayList<>();

}