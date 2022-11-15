package me.manylove.study.jpa.entitymanager;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Chapter 3. Persistence Context 기능 상세 보기
 * flush, detach, clear, close, merge
 *
 */
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Commit
public class Chapter003PersistenceContextToSeeMoreDetails {
    @Autowired
    private EntityManager em;
    private UUID flushTestId = UUID.fromString("3f30bd6d-2630-4098-8e94-238d18df9013");

    @Test
    @DisplayName("flush 직접 호출")
    @Order(1)
    void flush(){
        System.out.println("=== 영속화 전 ===");
        Member member = em.find(Member.class, flushTestId );
        System.out.println("=== 영속화 후 ===");

        System.out.println("=== 이름변경 전 ===");
        member.setName("강원화");
        System.out.println("=== 이름변경 후 ===");

        System.out.println("=== flush 전 ===");
        em.flush();
        System.out.println("=== flush 후 ===");

        System.out.println("=== 이름변경 전 ===");
        member.setName("강원천");
        System.out.println("=== 이름변경 후 ===");

        /**
         * [ 살펴볼 내용 ]
         * 1. flush를 호출함으로 인해, 중간에 변경된 내용이 쿼리로 날아 갔다.
         */
    }

    @Test
    @DisplayName("flush 직접 호출과 비교")
    @Order(2)
    void noFlush(){
        System.out.println("=== 영속화 전 ===");
        Member member = em.find(Member.class, flushTestId);
        System.out.println("=== 영속화 후 ===");

        System.out.println("=== 이름변경 전 ===");
        member.setName("강원화");
        System.out.println("=== 이름변경 후 ===");

        System.out.println("=== 이름변경 전 ===");
        member.setName("강원천");
        System.out.println("=== 이름변경 후 ===");

        /**
         * [ 살펴볼 내용 ]
         * 1. flush가 없는 경우는 최종 스냅샷 내용이 최종 엔티티 내용과 동일함으로 업데이트 쿼리를 발생시키지 않는다.
         */
    }

    @Test
    @DisplayName("new and detach")
    @Order(3)
    void newAndDetach(){
        UUID saveId1 = UUID.randomUUID();
        Member member1 = new Member(saveId1, "detachMe");

        System.out.println("=== 맴버1 영속화 전 ===");
        em.persist(member1);
        System.out.println("=== 맴버1 영속화 후 ===");

//        UUID saveId2 = UUID.randomUUID();
//        Member member2 = new Member(saveId2, "notDetach");
//
//        System.out.println("=== 맴버2 영속화 전 ===");
//        em.persist(member2);
//        System.out.println("=== 맴버2 영속화 후 ===");

        System.out.println("=== detach member1 전 ===");
        em.detach(member1);
        System.out.println("=== detach member1 후 ===");

        /**
         * [ 살펴 볼 내용 ]
         * 1. 새로운 객체를 영속화 한 후, 준영속 상태로 만들면 아무일도 일어나지 않는다.
         *    해당 객체에 대한 1차캐시, 쓰기 지연, 엔티티를 관리하기 위한 모든 정보가 제거되기 때문이다.
         */
    }

    @Test
    @DisplayName("detach")
    @Order(4)
    void findAndDetach(){
        UUID dongId = UUID.fromString("2e61abf6-3312-45d2-9e48-1e105a7fba0d");

        System.out.println("=== 오동규 조회 전 ===");
        Member memberOh = em.find(Member.class, dongId);
        System.out.println("=== 오동규 조회 후 ===");

        UUID broGuId = UUID.randomUUID();
        Member memberKimY = new Member(broGuId, "김연규");

        System.out.println("=== 김연규 영속화 전 ===");
        em.persist(memberKimY);
        System.out.println("=== 김연규 영속화 후 ===");

        System.out.println("=== 오동규 수정 전 ===");
        memberOh.setName("오동그라미");
        System.out.println("=== 오동그라미 수정 후 ===");

        System.out.println("=== detach 오동그라미 전 ===");
        em.detach(memberOh);
        System.out.println("=== detach 오동그라미 후 ===");

        /**
         * [ 살펴 볼 내용 ]
         * 1. 수정된 오동그라미는 DB에 반영되지 않고, Detach 되지 않은 김연규에 대한 insert 쿼리가 발생한다.
         */
    }

    @Test
    @DisplayName("clear")
    @Order(5)
    void clear(){
        UUID dongId = UUID.fromString("2e61abf6-3312-45d2-9e48-1e105a7fba0d");

        System.out.println("=== 오동규 조회 전 ===");
        Member memberOh = em.find(Member.class, dongId);
        System.out.println("=== 오동규 조회 후 ===");

        UUID saveId = UUID.randomUUID();
        Member member = new Member(saveId, "배상현");

        System.out.println("=== 배상현 영속화 전 ===");
        em.persist(member);
        System.out.println("=== 배상현 영속화 후 ===");

        System.out.println("=== 오동규 수정 전 ===");
        memberOh.setName("오동그라미");
        System.out.println("=== 오동그라미 수정 후 ===");

        System.out.println("=== clear 전 ===");
        em.clear();
        System.out.println("=== clear 후 ===");

        UUID comId = UUID.randomUUID();
        Member newMember = new Member(comId, "김은화");

        System.out.println("=== 김은화 영속화 전 ===");
        em.persist(newMember);
        System.out.println("=== 김은화 영속화 후 ===");

        System.out.println("=== 오동규 조회 전 ===");
        Member rememberOh = em.find(Member.class, dongId);
        System.out.println("=== 오동규 조회 후 ===");

        System.out.println("=== 오동그라미는 DB에 반영되지 않았음을 확인 !! ===");
        assertThat(rememberOh.getName()).isEqualTo("오동규");
        System.out.println("=== clear 이전에 조회해서 clear 이후에 조회한 비교 ===");
        assertThat(memberOh == rememberOh).isFalse();

        /**
         * [ 살펴 볼 내용 ]
         * 1. clear 후에 오동규를 조회하는 경우, 오동규를 조회하는 insert 문이 다시 발생한다.
         * 2. 김은화의 영속화 쿼리보다, 오동규의 조회 쿼리가 먼저 발생한다.
         * 3. clear 이전에 수정한 오동그라미는 DB에 반영되지 않았다.
         * 4. clear 이전에 조회해서 받은 오동규와 clear 이후에 조회한 오동규는 동일성이 보장되지 않는다.
         */
    }

    @Test
    @DisplayName("기존에 있는 객체 merge")
    @Order(6)
    void merge(){
        UUID young1000Id = UUID.fromString("91905d95-5161-4246-805e-d50960bef07b");
        Member memberLee = new Member(young1000Id, "이영1000");

        System.out.println("=== merge 전 ===");
        Member mergeMemberLee = em.merge(memberLee);
        System.out.println("=== merge 후 ===");

        System.out.println("=== memberLee 와 mergeMemberLee 동일성 검사 ===");
        assertThat(memberLee == mergeMemberLee).isFalse();

        System.out.println("=== 영속화 된 객체 내용 확인 ===");
        assertThat(mergeMemberLee.getName()).isEqualTo("이영1000");

        System.out.println("=== memberLee 이름 변경 전 ===");
        memberLee.setName("2영천");
        System.out.println("=== memberLee 이름 '2영천'으로 변경 후 ===");

        System.out.println("=== flush 실행 전 ===");
        em.flush();
        System.out.println("=== flush 실행 후 ===");

        System.out.println("=== mergeMemberLee 이름 변경 전 ===");
        mergeMemberLee.setName("이영천");
        System.out.println("=== mergeMemberLee 이름 '이영천'으로 변경 후 ===");

        /**
         * [ 살펴 볼 내용 ]
         * 1. merge를 통해 반환된 객체는 memberLee의 내용을 담고 있다.
         * 2. memberLee와 mergeMemberLee는 동일한 객체가 아니다.
         * 3. memberLee는 영속화 객체가 아니기 때문에 변경해도 변경감지가 일어나지 않는다.
         * 4. merge된 내용은 flush가 이루어지는 순간에 반영된다.
         * 5. 만약 flush 전에 '이영천'으로 내용을 변경하면, 처음 조회된 내용과 같기 때문에 update 쿼리가 발생하지 않는다.
         */
    }

    @Test
    @DisplayName("기존에 없는 객체 merge")
    @Order(7)
    void merge2(){
        UUID centuryId = UUID.randomUUID();
        Member memberJung = new Member(centuryId, "정헌기");

        System.out.println("=== merge 전 ===");
        Member mergeMemberJung = em.merge(memberJung);
        System.out.println("=== merge 후 ===");

        System.out.println("=== memberJung 와 mergeMemberJung 동일성 검사 ===");
        assertThat(memberJung == mergeMemberJung).isFalse();

        System.out.println("=== 영속화 된 객체 내용 확인 ===");
        assertThat(mergeMemberJung.getName()).isEqualTo("정헌기");

        System.out.println("=== memberJung 이름 변경 전 ===");
        memberJung.setName("정브로");
        System.out.println("=== memberJung 이름 '정브로'으로 변경 후 ===");

        System.out.println("=== mergeMemberJung 이름 변경 전 ===");
        mergeMemberJung.setName("정세기");
        System.out.println("=== mergeMemberJung 이름 '정세기'으로 변경 후 ===");

        System.out.println("=== mergeMemberJung 이름 변경 전 ===");
        mergeMemberJung.setName("신세기");
        System.out.println("=== mergeMemberJung 이름 '신세기'으로 변경 후 ===");

        /**
         * [ 살펴 볼 내용 ]
         * 1. merge 실행 시 id 검색결과가 없으면 영속화된 새로운 객체를 반환한다.
         * 2. flush 가 일어날 때 insert 쿼리가 발생하며, 변경이 있다면 최종변경된 내용이 한번만 반영된다.
         */
    }

    @Test
    @DisplayName("contains")
    @Order(8)
    void contains(){
        UUID dongId = UUID.fromString("2e61abf6-3312-45d2-9e48-1e105a7fba0d");
        Member memberOh = new Member(dongId, "오동그라미");

        System.out.println("=== merge 전 ===");
        Member mergeMemberLee = em.merge(memberOh);
        System.out.println("=== merge 후 ===");

        System.out.println("=== merge된 내용 확인 ===");
        assertThat(mergeMemberLee.getName()).isEqualTo("오동그라미");

        System.out.println("=== 영속 상태 검사 1 전 ===");
        assertThat(em.contains(memberOh)).isFalse();
        assertThat(em.contains(mergeMemberLee)).isTrue();
        System.out.println("=== 영속 상태 검사 1 후  ===");

        System.out.println("=== detach 실행 전 ===");
        em.detach(mergeMemberLee);
        System.out.println("=== detach 실행 후 ===");

        Member findMemberOh = em.find(Member.class, dongId);

        System.out.println("=== merge된 내용이 DB에 반영되지 않았음을 확인 ===");
        assertThat(findMemberOh.getName()).isNotEqualTo("오동그라미");

        System.out.println("=== 영속 상태 검사 2 전 ===");
        assertThat(em.contains(memberOh)).isFalse();
        assertThat(em.contains(mergeMemberLee)).isFalse();
        assertThat(em.contains(findMemberOh)).isTrue();
        System.out.println("=== 영속 상태 검사 2 후 ===");

        // 발표중 추가 테스트 구문.
//        Member merge = em.merge(mergeMemberLee);
//        assertThat(merge == findMemberOh).isTrue();


        /**
         * [ 살펴 볼 내용 ]
         * 1. merge 후에 반환된 객체는 merge 된 내용을 반영하고 있다.
         * 2. 그러나, em.detach를 실행하면 merge된 내용은 DB에 반영되지 않고 소멸된다.
         * 3. 해당 아이디로 재검색 시, DB에 merge된 내용이 반영되지 않은 것을 확인할 수 있다.
         * 4. contains 메소드를 이용하여 객체가 영속 상태인지 확인 할 수 있다.
         */
    }
}
