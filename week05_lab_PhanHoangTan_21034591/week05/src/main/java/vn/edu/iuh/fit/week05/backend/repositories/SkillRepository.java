package vn.edu.iuh.fit.week05.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.iuh.fit.week05.backend.models.Skill;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    @Query("SELECT s.skillName FROM Skill s WHERE s.id = :id")
    String findSkillNameById(@Param("id") Long id);

    @Query("SELECT s.skillName FROM Skill s")
    List<String> findAllSkillNames();
}