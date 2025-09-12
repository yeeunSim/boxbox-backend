package com.showrun.boxbox.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long likeSn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "radio_sn", nullable = false)
    private FanRadio fanRadio;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private Long likeUserSn;

    public Like(Long likeUserSn) {
        this.likeUserSn = likeUserSn;
    }

    @Builder
    public static Like create(FanRadio fanRadio, Long likeUserSn) {
        Like like = new Like(likeUserSn);
        like.addFanRadio(fanRadio);
        return like;
    }

    private void addFanRadio(FanRadio fanRadio){
        this.fanRadio = fanRadio;
        fanRadio.getLikes().add(this);
    }

    public void remove(FanRadio fanRadio){
        fanRadio.getLikes().remove(this);
    }
}
