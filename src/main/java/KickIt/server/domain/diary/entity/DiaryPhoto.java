package KickIt.server.domain.diary.entity;

import KickIt.server.global.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryPhoto extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    private String photoUrl;

    @ManyToOne
    @JoinColumn(name = "diary_id")
    private Diary diary;
}