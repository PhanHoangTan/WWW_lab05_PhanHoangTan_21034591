package vn.edu.iuh.fit.week05.frontend.controllers;

import com.neovisionaries.i18n.CountryCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.iuh.fit.week05.backend.models.Address;
import vn.edu.iuh.fit.week05.backend.models.Candidate;
import vn.edu.iuh.fit.week05.backend.models.CandidateSkill;
import vn.edu.iuh.fit.week05.backend.models.Skill;
import vn.edu.iuh.fit.week05.backend.repositories.AddressRepository;
import vn.edu.iuh.fit.week05.backend.repositories.CandidateRepository;
import vn.edu.iuh.fit.week05.backend.services.CandidateService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/candidates")
public class CandidateController {
    @Autowired
    private CandidateService candidateService;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private AddressRepository addressRepository;

    @GetMapping("/candidates")
    public String showCandidateListPaging(Model model,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        // Chuyển số trang từ 1-based (giao diện người dùng) sang 0-based (backend xử lý)
        int currentPage = Math.max(page, 1) - 1;
        int pageSize = Math.max(size, 1);

        // Lấy dữ liệu phân trang và sắp xếp
        Page<Candidate> candidatePage = candidateService.findAll(
                currentPage, pageSize, "id", "asc");
        model.addAttribute("candidatePage", candidatePage);

        // Chuẩn bị danh sách số trang
        int totalPages = candidatePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "candidates/candidates-paging"; // Trả về tên view hiển thị
    }


    @GetMapping("/list")
    public String showCandidateList(Model model) {
        model.addAttribute("candidates", candidateRepository.findAll());
        return "candidates/candidates";
    }

    @PostMapping("/add")
    public String addCandidate(@ModelAttribute("candidate") Candidate candidate,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("countries", CountryCode.values());
            return "candidates/add-candidate";
        }

        addressRepository.save(candidate.getAddress());
        candidateRepository.save(candidate);
        return "candidates/candidates-paging";
    }

    @GetMapping("/show-add-form")
    public String showAddCandidateForm(Model model) {
        Candidate candidate = new Candidate();
        candidate.setAddress(new Address());

        model.addAttribute("candidate", candidate);
        model.addAttribute("countries", CountryCode.values());

        return "candidates/add-candidate";
    }

    @GetMapping("/show-edit-form/{id}")
    public ModelAndView edit(@PathVariable("id") long id) {
        ModelAndView modelAndView = new ModelAndView();
        Optional<Candidate> opt = candidateRepository.findById(id);
        if (opt.isPresent()) {
            Candidate candidate = opt.get();
            modelAndView.addObject("candidate", candidate);
            modelAndView.addObject("countries", CountryCode.values());
            modelAndView.setViewName("candidates/update-candidates");
        } else {
            modelAndView.setViewName("redirect:/candidates?error=candidateNotFound");
        }
        return modelAndView;
    }


    @PostMapping("/update")
    public String update(
            @ModelAttribute("candidate") Candidate candidate,
            BindingResult result,
            Model model) {

        if (candidate.getAddress() == null || candidate.getAddress().getCountry() == null) {
            model.addAttribute("error", "Country is required.");
            model.addAttribute("candidate", candidate);
            model.addAttribute("countries", CountryCode.values());
            return "candidates/update-candidates";
        }

        Address address = candidate.getAddress();
        if (address.getId() == null) {
            addressRepository.save(address);
        } else {
            addressRepository.save(address);
        }
        candidate.setAddress(address);
        candidateRepository.save(candidate);
        return "candidates/candidates-paging";
    }

    @GetMapping("/search")
    public String searchCandidates(
            @RequestParam("keyword") String keyword,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            Model model) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<Candidate> candidatePage = candidateService.searchCandidates(
                keyword, currentPage - 1, pageSize, "id", "asc");

        model.addAttribute("candidatePage", candidatePage);
        model.addAttribute("keyword", keyword);

        int totalPages = candidatePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "candidates/candidates-paging";
    }
    @GetMapping("/view-skill/{id}")
    public String viewSkills(@PathVariable Long id, Model model) {
        Optional<Candidate> candidate = candidateRepository.findById(id);
        List<CandidateSkill> candidateSkills = candidateService.getCandidateSkillsByCandidateId(id);
        model.addAttribute("candidate", candidate.get());
        model.addAttribute("candidateSkills", candidateSkills);
        return "candidates/view-skill";
    }


    @GetMapping("/suggest-skill-to-learn/{id}")
    public String suggestSkillToLearn(@PathVariable Long id, Model model) {
        Candidate candidate = candidateRepository.findById(id).get();
        model.addAttribute("candidate", candidate);
        model.addAttribute("suggestedSkills", candidateService.suggestSkillToLearn(id));
        return "candidates/suggested-skill";
    }




}
