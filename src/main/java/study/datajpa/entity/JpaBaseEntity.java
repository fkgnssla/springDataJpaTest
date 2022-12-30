package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class JpaBaseEntity {

    @Column(updatable = false) //절대 변경되지 않는다.
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist //Persist(저장) 하기 전에 해당 메서드가 실행된다.
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate //Update(수정) 하기 전에 해당 메서드 실행된다.
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
