package jpabook.springdatajpa.repository;

import jakarta.persistence.EntityManager;
import jpabook.springdatajpa.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    // 화면에 맞춘 쿼리들은 커스텀을 사용하는 것보다는 따로 레파지토리를 분리해 두는 게 낫다.
    // 핵심 비즈니스 로직과, 화면에 맞춘 복잡한 쿼리의 분리.
    // 아키텍처 설계를 고민해 보자.
    private final EntityManager entityManager;

    List<Member> findAllMembers() {
        return entityManager.createQuery("select m from Member m", Member.class).getResultList();
    }

}
