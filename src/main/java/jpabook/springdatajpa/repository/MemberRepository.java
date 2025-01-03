package jpabook.springdatajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import jpabook.springdatajpa.dto.MemberDto;
import jpabook.springdatajpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {

    //List<Member> findByUsername(String username);

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    //@Query(name = "Member.findByUsername") 없어도 동작함, 네임드 쿼리는 실무에서 사용 잘 안 함
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findByUsernameList();

    @Query("select new jpabook.springdatajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

    // 웹 페이지 (토탈 카운트가 필요할 때) / 카운터 쿼리를 분리할 수도 있다.
    // 카운트는 조인을 안 해도 되는데, 컨텐츠 쿼리가 조인을 할 때 카운터 쿼리도 조인을 하기 때문에 따로 분리해야 한다.
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
    // 모바일 페이지 UI (더보기에서 +1 할 때, 리미트 간격을 1 추가해서 구해 준다. 토탈 카운트 쿼리는 따로 안 나간다.)
    // Slice<Member> findByAge(int age, Pageable pageable);
    // 리스트로도 동작한다.
    // List<Member> findByAge(int age, Pageable pageable);

    // 벌크성 수정 쿼리. ex) 해가 지나서 직원들의 나이를 한꺼번에 업데이트 하는 경우.
    // 수정은 모디파이 어노테이션을 넣어 준다. 어노테이션이 없으면 오류가 발생한다.
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age +1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"}) //JPQL로 하기 싫고, team 조회를 같이 조회하고 싶을 때 fetch join 같은 효과
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m") // 이런 식으로도 사용이 가능하다.
    List<Member> findMemberEntityGraph();

    // @EntityGraph(attributePaths = {"team"}) // 이런 식으로도 가능하다.
    @EntityGraph("Member.all") //Member Entity @NamedEntityGraph 참고
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    <T> List<T> findProjectionsByUsername(String username, Class<T> type);

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name as teamName" +
            " from Member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
