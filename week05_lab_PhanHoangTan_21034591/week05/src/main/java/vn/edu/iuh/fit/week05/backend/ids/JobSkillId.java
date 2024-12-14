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
@ToString
public class JobSkillId implements Serializable {
    private static final long serialVersionUID = 8186362411222001980L;
    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "skill_id", nullable = false)
    private Long skillId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        JobSkillId entity = (JobSkillId) o;
        return Objects.equals(this.jobId, entity.jobId) &&
                Objects.equals(this.skillId, entity.skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId, skillId);
    }

}