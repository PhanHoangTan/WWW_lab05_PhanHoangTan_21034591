package vn.edu.iuh.fit.week05.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.week05.backend.enums.SkillLevel;
import vn.edu.iuh.fit.week05.backend.models.Candidate;
import vn.edu.iuh.fit.week05.backend.models.JobSkill;

import java.util.List;
import java.util.Optional;
@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Page<Candidate> findByFullNameContainingOrPhoneContainingOrEmailContaining(
            String fullName, String phone, String email, Pageable pageable);

    @Query("SELECT c FROM Candidate c WHERE " +
            "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Candidate> searchCandidates(String keyword, Pageable pageable);

    @Query("""
    select c from Candidate c inner join c.candidateSkills candidateSkills
    where candidateSkills.skillLevel = ?1 and candidateSkills.skill.skillName = ?2
    """)
    List<Candidate> findBySkillLevelAndSkillName(Integer skillLevel, String skillName);




}