package vn.edu.iuh.fit.week05.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.week05.backend.models.JobSkill;
import vn.edu.iuh.fit.week05.backend.ids.JobSkillId;

import java.util.List;

public interface JobSkillRepository extends JpaRepository<JobSkill, JobSkillId> {
    List<JobSkill> findByJobId(Long jobId);
}