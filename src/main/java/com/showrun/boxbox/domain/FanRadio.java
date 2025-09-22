package com.showrun.boxbox.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "fan_radio")
public class FanRadio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long radioSn;

    @Column(nullable = false, updatable = false, length = 10)
    private String radioNickname;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String radioTextKor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String radioTextEng;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime radioCreatedAt;

    private int radioLikeCount;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean radioDeletedYn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_sn", nullable = false, columnDefinition = "INT UNSIGNED")
    private User user;

    @OneToMany(mappedBy = "fanRadio")
    private List<Like> likes = new ArrayList<>();


    public FanRadio(String radioNickname, String radioTextKor, String radioTextEng, int radioLikeCount, boolean radioDeletedYn) {
        this.radioNickname = radioNickname;
        this.radioTextKor = radioTextKor;
        this.radioTextEng = radioTextEng;
        this.radioLikeCount = radioLikeCount;
        this.radioDeletedYn = radioDeletedYn;
    }

    @Builder
    public static FanRadio create(User user, String radioNickname, String radioTextKor, String radioTextEng, int radioLikeCount, boolean radioDeletedYn){
        FanRadio fanRadio = new FanRadio(radioNickname, radioTextKor, radioTextEng, radioLikeCount, radioDeletedYn);
        fanRadio.addUser(user);
        return fanRadio;
    }

    private void addLike(Like like){
        this.likes.add(like);
    }

    public void addUser(User user) {
        this.user = user;
        user.getFanRadios().add(this); // ✅ 주인쪽에서만 양방향 연결
    }

    public FanRadio update(String radioTextKor, String radioTextEng){
        this.radioTextKor = radioTextKor;
        this.radioTextEng = radioTextEng;
        return this;
    }

    public void remove(Like like){
        this.likes.remove(like);
    }

    public void subtractLikeCount() {
        this.radioLikeCount -= 1;
    }

    public void addLikeCount(Integer like) {
        this.radioLikeCount = like;
    }

}
