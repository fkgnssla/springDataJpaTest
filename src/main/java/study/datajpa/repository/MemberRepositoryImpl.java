package study.datajpa.repository;

import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

//구현체의 이름은 MemberRepository가 MemberRepositoryCustom를 상속받아 쓰므로
//MemberRepository(Spring Data Jpa 상속)가 찾을수 있게끔 MemberRepository + Impl 형태로 생성해야한다.
//그러면 Spring Data Jpa가 인식해서 MemberRepositoryImpl을 스프링 빈으로 등록한다.
//MemberRepositoryCustom은 아무 이름을 해도 상관없다. 단순 인터페이스이다.
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
