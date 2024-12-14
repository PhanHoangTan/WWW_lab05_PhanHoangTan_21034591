package vn.edu.iuh.fit.week05.backend.models;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.iuh.fit.week05.backend.enums.SkillLevel;
import vn.edu.iuh.fit.week05.backend.ids.CandidateSkillId;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "candidate_skill")
public class CandidateSkill {
    @EmbeddedId
    private CandidateSkillId id;

    @MapsId("skillId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @MapsId("canId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "can_id", nullable = false)
    private Candidate can;

    @Column(name = "more_infos", length = 1000)
    private String moreInfos;

    @Column(name = "skill_level")
    private Integer skillLevel;

    @Transient
    public SkillLevel getSkillLevel() {
        return SkillLevel.fromCode(skillLevel);
    }

    public void setSkillLevel(SkillLevel skillLevel) {
        this.skillLevel = skillLevel != null ? skillLevel.ordinal() : null;
    }
}