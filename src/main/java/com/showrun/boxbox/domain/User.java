package com.showrun.boxbox.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long userSn;

    @Column(nullable = false, updatable = false, length = 10)
    private String userNickname;

    @Column(nullable = false, updatable = false)
    private LocalDate userBirth;

    @Enumerated(EnumType.STRING)
    private Gender userGender;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean svcUsePcyAgmtYn;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean psInfoProcAgmtYn;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean userDeletedYn;

    @Enumerated(EnumType.STRING)
    private Status userStatus;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime userCreatedAt;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime userLastLoginAt;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    private Login login;

    @OneToMany(mappedBy = "user")
    private List<FanRadio> fanRadios = new ArrayList<>();

    @Column(nullable = false)
    private boolean userLang = false;

    public User(String userNickname, LocalDate userBirth, Gender userGender, boolean svcUsePcyAgmtYn, boolean psInfoProcAgmtYn, boolean userDeletedYn, Status userStatus, boolean userLang) {
        this.userNickname = userNickname;
        this.userBirth = userBirth;
        this.userGender = userGender;
        this.svcUsePcyAgmtYn = svcUsePcyAgmtYn;
        this.psInfoProcAgmtYn = psInfoProcAgmtYn;
        this.userDeletedYn = userDeletedYn;
        this.userStatus = userStatus;
        this.userLang = userLang;
    }

    @Builder
    public static User create(Login login, String userNickname, LocalDate userBirth, Gender userGender, boolean svcUsePcyAgmtYn, boolean psInfoProcAgmtYn, boolean userDeletedYn, Status userStatus, boolean userLang) {
        User user = new User(userNickname, userBirth, userGender, svcUsePcyAgmtYn, psInfoProcAgmtYn, userDeletedYn, userStatus, userLang);
        user.addLogin(login);
        return user;
    }

    public void addLogin(Login login) {
        this.login = login;
        login.addUser(this);
    }

    public void addFanRadio(FanRadio fanRadio) {
        this.fanRadios.add(fanRadio);
    }

    public User update(Status userStatus, LocalDateTime userLastLoginAt) {
        this.userStatus = userStatus;
        this.userLastLoginAt = userLastLoginAt;
        return this;
    }

    public User updateLang(boolean userLang) {
        this.userLang = userLang;

        return this;
    }
}