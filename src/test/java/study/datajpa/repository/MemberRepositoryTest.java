package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

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
}