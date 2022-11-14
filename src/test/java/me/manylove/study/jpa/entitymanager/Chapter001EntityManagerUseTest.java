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
 * Chapter 1. EntityManager 기본 사용법
 *
 * 테스트 시, 순서대로 실행하지 않으면 에러가 발생 할 수 있어요 !
 */
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Commit
public class Chapter001EntityManagerUseTest {
    @Autowired
    private EntityManager em;
    private UUID testId = UUID.fromString("40d9f800-e64b-49f9-8654-a46b4d177af6");

    @Test
    @DisplayName("1.저장 (C)")
    @Order(1)
    void persist(){
        Member member = new Member(testId, "권다애");
        em.persist(member);
    }

    @Test
    @DisplayName("2.조회 (R)")
    @Order(2)
    void find(){
        Member memberKwon = em.find(Member.class, testId);
        assertThat(memberKwon.getName()).isEqualTo("권다애");
    }

    @Test
    @DisplayName("3.수정 (U)")
    @Order(3)
    void update(){
        Member memberKwon = em.find(Member.class, testId);
        memberKwon.setName("권영애");
    }

    @Test
    @DisplayName("4.삭제 (D)")
    @Order(4)
    void delete(){
        Member memberKwon = em.find(Member.class, testId);
        em.remove(memberKwon);
    }
}
