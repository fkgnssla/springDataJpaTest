package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        memberRepository.save(member);
        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember.getId()).isEqualTo(member.getId());
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 =
                memberRepository.findById(member1.getId()).get();
        Member findMember2 =
                memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<Member> result =
                memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findTestBy() {
        Member u1 = new Member("u1", 10);
        Member u2 = new Member("u2", 20);
        memberRepository.save(u1);
        memberRepository.save(u2);

        List<Member> testBy = memberRepository.findTestBy(); //전체 조회
        for (Member member : testBy) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void testQuery() {
        Member u1 = new Member("u1", 10);
        Member u2 = new Member("u2", 20);
        memberRepository.save(u1);
        memberRepository.save(u2);

        List<Member> result = memberRepository.findUser("u1", 10);
        assertThat(result.get(0)).isEqualTo(u1);
    }

    @Test
    public void findUsernameList() {
        Member u1 = new Member("u1", 10);
        Member u2 = new Member("u2", 20);
        memberRepository.save(u1);
        memberRepository.save(u2);

        List<String> result = memberRepository.findUsernameList();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member u1 = new Member("u1", 10);
        memberRepository.save(u1);
        u1.changeTeam(team);

        List<MemberDto> result = memberRepository.findMemberDto();
        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findByNames() {
        Member u1 = new Member("u1", 10);
        Member u2 = new Member("u2", 20);
        memberRepository.save(u1);
        memberRepository.save(u2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("u1", "u2"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member u1 = new Member("u1", 10);
        Member u2 = new Member("u2", 20);
        memberRepository.save(u1);
        memberRepository.save(u2);

        memberRepository.findListByUsername("u1"); //컬렉션
        memberRepository.findMemberByUsername("u1"); //단건
        memberRepository.findOptionalByUsername("u1"); //단건 Optional
    }

    @Test
    public void paging() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        //0페이지부터 3개 가져온다.(0,1,2페이지) + 이름으로 역순 정렬
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        Page<MemberDto> pageMemberDto = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null)); //Dto로 변환

        //then
        List<Member> content = page.getContent(); //가져온 데이터
        long totalElements = page.getTotalElements(); //나이가 같은 데이터의 총 개수

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3); //가져온 데이터 개수(5개 중 3개)
        assertThat(totalElements).isEqualTo(5); //데이터 총 개수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //페이지 개수
        assertThat(page.isFirst()).isTrue(); //첫 번째 페이지냐? => True
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있냐? => True
    }

    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20); //데이터의 age가 파라미터 age이상이면 +1
//        em.clear(); => 대신 bulkAgePlus()의 @Modifying에 clearAutomatically = true를 선언하면 실행 후 자동으로 영속성 컨텍스트를 비운다.

        List<Member> result = memberRepository.findByUsername("member5");
        Member member = result.get(0); //이 객체의 age는 40일까 41일까?(em.clear(),clearAutomatically = true 전이라고 가정) => 40
        //(DB:"member5", 41), (영속성 컨텍스트:"member5", 40) 현 시점 상태!
        //findByUsername()을 날리면 DB에서 조회하여 41을 가져오지만 DB와 영속성 컨텍스트의 값이 달라 충돌한다.
        //JPA는 영속성 컨텍스트의 동일성을 보장한다.
        //따라서 DB의 결과 값을 버리고, 1차 캐시에 있는 결과값을 반환하기에 40이 반환된다.
        //하지만! 이전에 em.clear()를 하면 영속성 컨텍스트를 비우기에 충돌이 나지않아 DB의 값인 41이 잘 반환된다.
        System.out.println("member = " + member);
        
        //then
        assertThat(resultCount).isEqualTo(3); //업데이트 된 데이터 개수(응답값)
    }

    @Test
    public void findMemberLazy() throws Exception {
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));
        em.flush();
        em.clear();

        //when
//        List<Member> members = memberRepository.findMemberFetchJoin();
        List<Member> members = memberRepository.findAll();

        //then
        for (Member member : members) {
            member.getTeam().getName();
            System.out.println("member = " + member);
        }
    }
}