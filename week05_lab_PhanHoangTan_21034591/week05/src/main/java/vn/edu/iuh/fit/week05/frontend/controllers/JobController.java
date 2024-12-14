package vn.edu.iuh.fit.week05.frontend.controllers;

import com.neovisionaries.i18n.CountryCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.iuh.fit.week05.backend.enums.SkillLevel;
import vn.edu.iuh.fit.week05.backend.ids.JobSkillId;
import vn.edu.iuh.fit.week05.backend.models.*;
import vn.edu.iuh.fit.week05.backend.repositories.*;
import vn.edu.iuh.fit.week05.backend.services.CandidateService;
import vn.edu.iuh.fit.week05.backend.services.EmailService;
import vn.edu.iuh.fit.week05.backend.services.JobService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/jobs")
public class JobController {
    @Autowired
    private JobService jobService;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private CandidateService candidateServices;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private JobSkillRepository jobSkillRepository;


    @GetMapping("/jobs")
    public String showJobListPaging(Model model,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        // Chuyển số trang từ 1-based (giao diện người dùng) sang 0-based (backend xử lý)
        int currentPage = Math.max(page, 1) - 1;
        int pageSize = Math.max(size, 1);

        // Lấy dữ liệu phân trang
        Page<Job> jobPage = jobService.findPaginated(PageRequest.of(currentPage, pageSize));
        model.addAttribute("jobPage", jobPage);

        // Chuẩn bị danh sách số trang (nếu có nhiều hơn 1 trang)
        int totalPages = jobPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "jobs/jobs-paging"; // Trả về tên view hiển thị
    }


    @GetMapping("/list")
    public String showJobList(Model model) {
        model.addAttribute("jobs", jobRepository.findAll());
        return "jobs/jobs";
    }

    @GetMapping("/view-company/{companyId}")
    public String viewCompany(@PathVariable Long companyId, Model model) {
        Optional<Company> company = companyRepository.findById(companyId);
        if (company.isPresent()) {
            Company companyEntity = company.get();
            model.addAttribute("company", companyEntity);
            List<Job> jobs = jobRepository.findByCompanyId(companyId);
            model.addAttribute("jobs", jobs);
        } else {
            model.addAttribute("company", new Company());
            model.addAttribute("jobs", Collections.emptyList());
        }
        return "jobs/view-company";
    }
    @GetMapping("/search")
    public String searchJobs(
            @RequestParam("keyword") String keyword,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            Model model) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<Job> jobPage = jobService.searchJobs(
                keyword, currentPage - 1, pageSize, "id", "asc");

        model.addAttribute("jobPage", jobPage);
        model.addAttribute("keyword", keyword);

        int totalPages = jobPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "jobs/jobs-paging";
    }
    @GetMapping("/view-skill-request/{id}")
    public String viewSkillRequest(@PathVariable Long id, Model model) {
        Optional<Job> job = jobRepository.findById(id);
        if (job.isPresent()) {
            List<JobSkill> jobSkills = jobService.getSkillsByJobId(id);
            model.addAttribute("job", job.get());
            model.addAttribute("jobSkills", jobSkills);
        } else {
            model.addAttribute("job", null);
            model.addAttribute("jobSkills", Collections.emptyList());
        }
        return "jobs/view-skill-request";
    }

    @GetMapping("/view-candidatebyskill/{id}")
    public ModelAndView viewCandidateBySkill(@PathVariable("id") long jobId) {
        ModelAndView mav = new ModelAndView("jobs/view-candidate");
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        List<Candidate> candidates = candidateServices.findCandidatesForJob(jobId);
        mav.addObject("job", job);
        mav.addObject("listCandidate", candidates);
        return mav;
    }

    @GetMapping("/{jobId}/{candidateId}/send-email")
    public String sendEmail(@PathVariable("jobId") Long jobId, @PathVariable("candidateId") Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        String subject = "Job Application for " + candidate.getFullName();
        String body = "Hello " + candidate.getFullName() + ",\n\nWe are pleased to inform you about the job opportunity at our company. Please visit our website for more details.";

        sendEmail(candidate.getEmail(), subject, body);

        return "redirect:/jobs?success=applySuccess";


    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("phanhoangtan.job@gmail.com");
        mailSender.send(message);
    }


    @GetMapping("/apply/{id}")
    public String applyJob(@PathVariable("id") long id) {
        return "redirect:/jobs?success=applySuccess";
    }


    @GetMapping("/show-add-form/{companyId}")
    public ModelAndView showAddForm(@PathVariable Long companyId, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        modelAndView.addObject("job", new Job());
        modelAndView.addObject("skills", skillRepository.findAll());
        modelAndView.addObject("companyId", companyId);
        modelAndView.addObject("company", company);
        modelAndView.setViewName("jobs/add-job");
        return modelAndView;
        
    }
    @GetMapping("/show-edit-form/{companyId}/{jobId}")
    public ModelAndView showEditForm(@PathVariable Long companyId, @PathVariable Long jobId, Model model) {
        ModelAndView modelAndView = new ModelAndView();

        // Retrieve the company and job
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Add the job and skills to the model
        modelAndView.addObject("job", job);
        modelAndView.addObject("skills", skillRepository.findAll());
        modelAndView.addObject("companyId", companyId);
        modelAndView.addObject("company", company);

        modelAndView.setViewName("jobs/edit-job"); // Assuming you have an "edit-job.html" template
        return modelAndView;
    }

    @PostMapping("/update-job/{companyId}/{jobId}")
    public String updateJob(@PathVariable Long companyId, @PathVariable Long jobId,
                            @RequestParam("jobName") String jobName,
                            @RequestParam("jobDesc") String jobDesc,
                            @RequestParam("skills") List<Long> skills,
                            @RequestParam("skillLevels") List<String> skillLevels,
                            @RequestParam("more_infos") List<String> moreInfos) {
        // Retrieve the existing job to update
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Set updated fields
        job.setJobName(jobName);
        job.setJobDesc(jobDesc);

        // Update the associated skills
        List<JobSkill> jobSkills = new ArrayList<>();
        for (int i = 0; i < skills.size(); i++) {
            JobSkill jobSkill = new JobSkill();
            JobSkillId jobSkillId = new JobSkillId();
            jobSkillId.setJobId(jobId);
            jobSkillId.setSkillId(skills.get(i));
            jobSkill.setId(jobSkillId);
            jobSkill.setJob(job);
            jobSkill.setMoreInfos(moreInfos.get(i));
            jobSkill.setSkill(skillRepository.findById(skills.get(i))
                    .orElseThrow(() -> new RuntimeException("Skill not found")));
            jobSkill.setSkillLevel(SkillLevel.valueOf(skillLevels.get(i)));
            jobSkills.add(jobSkill);
            jobSkillRepository.save(jobSkill);
        }

        // Save the updated job
        job.setJobSkills(jobSkills);
        jobRepository.save(job);

        return "redirect:/companies/view-company/" + companyId;
    }





//    @GetMapping("/add/{id}")
//    public ModelAndView showAddJobForm(@PathVariable("id") Long companyId) {
//        ModelAndView modelAndView = new ModelAndView();
//        List<Skill> skills = skillRepository.findAll();
//        modelAndView.addObject("skills", skillRepository.findAll());
//
//        modelAndView.setViewName("jobs/add-job");
//        return modelAndView;
//
//
//    }

    @GetMapping("/skill-name/{id}")
    public String getSkillName(@PathVariable("id") Long skillId) {
        return skillRepository.findSkillNameById(skillId);
    }
    @PostMapping("/save")
    public String saveJob(@RequestParam("jobName") String jobName,
                          @RequestParam("jobDesc") String jobDesc,
                          @RequestParam("skills") List<Long> skills,
                          @RequestParam("skillLevels") List<String> skillLevels,
                          @RequestParam("more_infos") List<String> moreInfos,
                          @RequestParam("companyId") Long companyId) {
        Job job = new Job();
        job.setJobName(jobName);
        job.setJobDesc(jobDesc);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        job.setCompany(company);

        Job savedJob = jobRepository.save(job);

        List<JobSkill> jobSkills = new ArrayList<>();
        for (int i = 0; i < skills.size(); i++) {
            JobSkill jobSkill = new JobSkill();
            JobSkillId jobSkillId = new JobSkillId();
            jobSkillId.setJobId(savedJob.getId());
            jobSkillId.setSkillId(skills.get(i));
            jobSkill.setId(jobSkillId);
            jobSkill.setJob(savedJob);
            jobSkill.setMoreInfos(moreInfos.get(i));
            jobSkill.setSkill(skillRepository.findById(skills.get(i))
                    .orElseThrow(() -> new RuntimeException("Skill not found")));
            jobSkill.setSkillLevel(SkillLevel.valueOf(skillLevels.get(i)));
            jobSkills.add(jobSkill);
            jobSkillRepository.save(jobSkill);
        }

        savedJob.setJobSkills((List<JobSkill>) jobSkills);
        jobRepository.save(savedJob);

        System.out.println("Skills: " + jobSkills);

        return "redirect:/jobs/jobs";
    }

}
