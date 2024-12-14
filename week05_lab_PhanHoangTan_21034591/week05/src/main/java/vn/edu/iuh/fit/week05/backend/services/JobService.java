package vn.edu.iuh.fit.week05.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.week05.backend.models.Job;
import vn.edu.iuh.fit.week05.backend.models.JobSkill;
import vn.edu.iuh.fit.week05.backend.models.Skill;
import vn.edu.iuh.fit.week05.backend.repositories.JobRepository;
import vn.edu.iuh.fit.week05.backend.repositories.JobSkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobSkillRepository jobSkillRepository;
    public Page<Job> findAll(int pageNo, int pageSize, String sortBy,
                             String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return jobRepository.findAll(pageable);//findFirst.../findTop...
    }

    public Page<Job> findPaginated(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    public Page<Job> searchJobs(String keyword, int pageNo, int pageSize, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return jobRepository.searchJobs(keyword, pageable);
    }
    public List<JobSkill> getSkillsByJobId(Long jobId) {
        return jobSkillRepository.findByJobId(jobId);
    }

    public Job findById(Long id) {
        return jobRepository.findById(id).get();
    }
}
