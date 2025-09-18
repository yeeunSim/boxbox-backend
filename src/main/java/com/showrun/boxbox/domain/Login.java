package com.showrun.boxbox.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "login")
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long loginSn;

    @Column(nullable = false, updatable = false, length = 30)
    private String loginEmail;

    @Column(nullable = false, length = 30)
    private String loginPassword;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime tokenExp;

    @Column(nullable = false, length = 255)
    private String tokenValue;

    @Column(nullable = false, length = 32)
    private String salt;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_sn", unique = true, nullable = false, columnDefinition = "INT UNSIGNED")
    private User user;

    public Login(String loginEmail, String loginPassword, String tokenValue, String salt) {
        this.loginEmail = loginEmail;
        this.loginPassword = loginPassword;
        this.tokenValue = tokenValue;
        this.salt = salt;
    }

    @Builder
    public static Login create(User user, String loginEmail, String loginPassword, String tokenValue, String salt) {
        Login login = new Login(loginEmail, loginPassword, tokenValue, salt);
        login.addUser(user);
        return login;
    }

    public void addUser(User user) {
        this.user = user;
        user.addLogin(this);
    }

    public void update(String loginPassword, String tokenValue, String salt) {
        this.loginPassword = loginPassword;
        this.tokenValue = tokenValue;
        this.salt = salt;
    }

    public void update(String tokenValue, LocalDateTime tokenExp) {
        this.tokenValue = tokenValue;
        this.tokenExp = tokenExp;
    }

}
