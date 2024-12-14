package vn.edu.iuh.fit.week05.backend.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
//@ToString
public class CandidateSkillId implements Serializable {
    private static final long serialVersionUID = 8293631249931858708L;
    @Column(name = "skill_id", nullable = false)
    private Long skillId;

    @Column(name = "can_id", nullable = false)
    private Long canId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CandidateSkillId that = (CandidateSkillId) o;
        return Objects.equals(skillId, that.skillId) && Objects.equals(canId, that.canId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillId, canId);
    }
}