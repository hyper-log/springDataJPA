package jpabook.springdatajpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {

    // 만약 ID를 임의 채번으로 정한다고 할 때, JPA 기본으로 제공되는 save 메서드는
    // id가 null 기준으로 new Entity 판단하고, 임의 채번처럼 id가 정해져서 들어가는 경우
    // null 아닌 것으로 판단하여 merge 한다. (이때, 머지는 디비에 이미 있다는 가정으로 동작하기 때문에 셀렉트 호출을 한다.)
    // 이러면 굉장히 비효율적으로 되어 있어서, 새롭게 뉴 엔티티인지 아닌지 정의해 주는 과정이 필요하다.
    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
