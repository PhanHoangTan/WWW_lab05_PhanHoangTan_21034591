package vn.edu.iuh.fit.week05.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import vn.edu.iuh.fit.week05.backend.models.CandidateSkill;
import vn.edu.iuh.fit.week05.backend.ids.CandidateSkillId;

import java.util.List;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, CandidateSkillId>, PagingAndSortingRepository<CandidateSkill, CandidateSkillId> {
    List<CandidateSkill> findByIdCanId(Long candidateId);
}