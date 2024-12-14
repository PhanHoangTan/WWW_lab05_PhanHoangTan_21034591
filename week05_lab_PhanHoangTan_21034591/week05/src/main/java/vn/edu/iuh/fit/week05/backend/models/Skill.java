package vn.edu.iuh.fit.week05.backend.models;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.iuh.fit.week05.backend.enums.SkillLevel;
import vn.edu.iuh.fit.week05.backend.enums.SkillType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
//@ToString
@Table(name = "skill")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id", nullable = false)
    private Long id;

    @Column(name = "skill_description")
    private String skillDescription;

    @Column(name = "skill_name")
    private String skillName;


    @Column(name = "type")
    private Integer typeCode;

    @Transient // Không ánh xạ trực tiếp vào database
    public SkillType getType() {
        return SkillType.fromCode(typeCode);
    }

    public void setType(SkillType skillType) {
        this.typeCode = skillType != null ? skillType.ordinal() : null;
    }

    @OneToMany(mappedBy = "skill")
    private List<CandidateSkill> candidateSkills = new ArrayList<>();

    @OneToMany(mappedBy = "skill")
    private List<JobSkill> jobSkills = new ArrayList<>();
}