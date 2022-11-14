package me.manylove.study.jpa.entitymanager;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;

/**
 * Chapter 4. Persistence Context Exception 살펴보기
 *
 * 의도와 다르게 잘못 사용하면 어떤 결과가 올까?
 */
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Commit
public class Chapter004PersistenceContextExceptionTest {
    @Autowired
    private EntityManager em;

    // 다음 기회에 확인해 볼까?

    // 기존에 있는 ID로 em.persist 한다면?
    // 영속화 과정 없이 remove 호출 한다면?
    // 없는 객체를 가져와 달라고 요구 한다면?

}
