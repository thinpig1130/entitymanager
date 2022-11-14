package me.manylove.study.jpa.entitymanager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Chapter 2. Persistence Context 특징 살펴보기
 * 1차 캐시, 동일성 보장, 트랜잭션을 지원하는 쓰기 지연, 변경감지
 *
 * 순서 3,4,5,6 연속 실행 해야 에러가 없음.
 */
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Commit
public class Chapter002PersistenceContextHowItWorks {
    @Autowired
    private EntityManager em;

    private UUID testId = UUID.fromString("82a6dcaf-4269-4b37-a48c-2b9ed41db3a7");

    @Test
    @DisplayName("[객체 생성/저장] 비영속(new/transient) -> 영속(managed) ")
    @Order(1)
    void persist(){

        UUID saveId1 = UUID.randomUUID();
        Member member1 = new Member(saveId1, "영속화1");

        System.out.println("=== 맴버1 영속화 전 ===");
        em.persist(member1);
        System.out.println("=== 맴버1 영속화 후 ===");

        UUID saveId2 = UUID.randomUUID();
        Member member2 = new Member(saveId2, "영속화2");

        System.out.println("=== 맴버2 영속화 전 ===");
        em.persist(member2);
        System.out.println("=== 맴버2 영속화 후 ===");

        System.out.println("=== 맴버1 조회 전 ===");

        Member findMember = em.find(Member.class, saveId1);
        System.out.println("=== 맴버1 조회 후 ===");

        System.out.println("=== 동일성 검사 전 ===");
        // 동일성 검사
        System.out.println("=== member : " + member1);
        System.out.println("=== findMember : " + findMember);
        assertThat(member1).isEqualTo(findMember);
        assertThat(member1 == findMember).isTrue();
        System.out.println("=== 동일성 검사 후 ===");

        /**
         * [ 살펴볼 내용 ]
         * 1. 영속화가 컨텍스트에 저장 한 후에, 바로 INSERT 쿼리를 실행하지 않는다. ( 영속화 != DB저장 )
         * 2. 동일한 트랜잭션 내에서 저장 한 객체를 재조회하는 경우, 조회 쿼리가 발생하지 않는다. (1차 캐시를 이용한 조회)
         * 3. 새로운 객체를 저장한 후, 동일한 ID 값으로 엔티티를 조회 할 경우 같은 엔티티를 반환한다.(동일성 보장)
         * 4. 영속화 객체가 실제 DB에 저장되는 시점은 트랜잭션 commit 이 발생하는 시점이다.
         *    트랜잭션 commit이 발생하는 시점에 저장 쿼리를 모아서 전송한다. (트랜잭션 내의 쓰기 지연)
         */
    }

    @Test
    @DisplayName("[객체 조회] 준영속(detached) -> 영속(managed) ")
    @Order(2)
    void find(){

        UUID young1000Id = UUID.fromString("91905d95-5161-4246-805e-d50960bef07b");

        System.out.println("=== 조회 전 ===");
        Member memberLee = em.find(Member.class, young1000Id);
        System.out.println("=== 조회 후 ===");

        System.out.println("=== 재조회 전 ===");
        Member reMemberLee = em.find(Member.class, young1000Id);
        System.out.println("=== 재조회 후 ===");

        UUID flowerId = UUID.fromString("b0131310-42d8-4fc5-a123-273e01467268");
        System.out.println("=== 다른 맴버 조회 전 ===");
        Member memberKang = em.find(Member.class, flowerId);
        System.out.println("=== 다른 맴버 조회 후 ===");

        System.out.println("=== 재재조회 전 ===");
        Member rereMemberLee = em.find(Member.class, young1000Id);
        System.out.println("=== 재재조회 후 ===");

        // 동일성 검사
        assertThat(memberLee).isEqualTo(reMemberLee);
        assertThat(memberLee == reMemberLee).isTrue();
        assertThat(memberLee).isEqualTo(rereMemberLee);
        assertThat(memberLee == rereMemberLee).isTrue();
        assertThat(memberLee).isNotEqualTo(memberKang);
        assertThat(memberLee == memberKang).isFalse();

        /**
         * [ 살펴볼 내용 ]
         * 1. 1차 캐시에 없는 데이터는 영속화를 위해 DB에서 결과를 조회해 온다. ( 준영속상태 -> 영속상태로 전환 )
         * 2. 1차 캐시에 존재하는 경우, DB 쿼리를 이용한 재 조회를 하지 않는다. ( 성능 UP )
         * 3. 한 트랜잭션내의 같은 식별자를 가진 데이터는 동일한 객체 결과를 얻을 수 있다. (동일성 보장)
         */

    }

    @Test
    @DisplayName("[객체 등록 후 수정] ")
    @Order(3)
    void createAndUpdate(){
        Member member = new Member(testId, "강원화");

        System.out.println("=== 영속화 전 ===");
        em.persist(member);
        System.out.println("=== 영속화 후 ===");

        System.out.println("=== 이름변경 전 ===");
        member.setName("이원화");
        System.out.println("=== 이름변경 후 ===");

        /**
         * [ 살펴볼 내용 ]
         * 1. insert, update 쿼리 트랜잭션 커밋 시점에 순차 발생. ( 트랜잭션 내의 쓰기 지연 )
         * 2. member 이름 값 변경에 의한 업데이트 쿼리 자동 발생. ( 변경 감지 )
         */
    }

    @Test
    @DisplayName("[객체 조회 후 수정] ")
    @Order(4)
    void searchAndUpdate(){
        System.out.println("=== 영속화 전 ===");
        Member member = em.find(Member.class, testId);
        System.out.println("=== 영속화 후 ===");

        System.out.println("=== 이름변경 전 ===");
        member.setName("강원화");
        System.out.println("=== 이름변경 후 ===");

        System.out.println("=== 이름변경 전 ===");
        member.setName("강원천");
        System.out.println("=== 이름변경 후 ===");

        /**
         * [ 살펴볼 내용 ]
         * 1. 로직상 변경은 2번 발생했으나, 업데이트 쿼리는 최종 결과 한번만 발생 ( 스냅샷에 의한 변경 감지 )
         */
    }

    @Test
    @DisplayName("[객체 조회 후 수정 후 값 원상 복구] ")
    @Order(5)
    void searchAndUpdateAndRestore(){
        System.out.println("=== 영속화 전 ===");
        Member member = em.find(Member.class, testId);
        System.out.println("=== 영속화 후 ===");

        System.out.println("=== 이름변경 전 ===");
        member.setName("강원화");
        System.out.println("=== 이름변경 후 ===");

        System.out.println("=== 이름변경 전 ===");
        member.setName("강원천");
        System.out.println("=== 이름변경 후 ===");

        /**
         * [ 살펴볼 내용 ]
         * 1. 처음 영속화 시 내용과 최종 변경 내용에 차이가 없으므로 업데이트 쿼리가 발생하지 않음 ( 스냅샷에 의한 변경 감지 )
         */
    }

    @Test
    @DisplayName("[삭제] ")
    @Order(6)
    void delete(){
        System.out.println("=== 영속화 전 ===");
        Member member = em.find(Member.class, testId);
        System.out.println("=== 영속화 후 ===");

        System.out.println("=== 삭제 전 ===");
        em.remove(member);
        System.out.println("=== 삭제 후 ===");

        System.out.println("=== 삭제 후, 삭제 된 객체 이름 출력 : " +  member.getName());

        System.out.println("=== 조회 전 ===");
        Member findMember = em.find(Member.class, testId);
        System.out.println("=== 조회 후 ===");

        System.out.println("=== 결과 확인 전 ===");
        assertThat(findMember).isNull();
        System.out.println("=== 결과 확인 후 ===");

        /**
         * [ 살펴볼 내용 ]
         * 1. 삭제 실행 후에도, 삭제된 객체의 인스턴스 자체는 소멸되지 않음. ( 재사용하지 말고 가비지 컬렉션의 대상이 되도록 하는 것이 좋음 )
         * 2. 삭제 후에는 영속화 컨텍스트에서는 해당 객체를 반환하지 않음. ( 영속성 컨텍스트에서 즉시 제거 )
         * 3. 실제 삭제 쿼리는 트랜잭션 커밋 시점에 실행.
         */
    }
}
