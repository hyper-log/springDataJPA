package jpabook.springdatajpa.repository;

import jpabook.springdatajpa.dto.MemberDto;
import jpabook.springdatajpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

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
    // 카운트는 조인을 안 해도 됨, 그런데 원래 컨텐츠 쿼리가 조인을 할 때 카운터 쿼리도 조인을 하기 때문에 따로 분리해야 한다.
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
    // 모바일 페이지 UI (더보기에서 +1...... 할 때)
    // Slice<Member> findByAge(int age, Pageable pageable);
    // 리스트로도 동작한다.
    // List<Member> findByAge(int age, Pageable pageable);

}
