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
import vn.edu.iuh.fit.week05.backend.models.Company;
import vn.edu.iuh.fit.week05.backend.models.Job;
import vn.edu.iuh.fit.week05.backend.repositories.AddressRepository;
import vn.edu.iuh.fit.week05.backend.repositories.CompanyRepository;
import vn.edu.iuh.fit.week05.backend.repositories.JobRepository;
import vn.edu.iuh.fit.week05.backend.services.CandidateService;
import vn.edu.iuh.fit.week05.backend.services.CompanyService;
import vn.edu.iuh.fit.week05.backend.services.JobService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/companies")
public class CompanyController {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobService jobServices;
    @Autowired
    private CandidateService candidateServices;


    @GetMapping("/list")
    public String showCompanyList(Model model) {
        model.addAttribute("companies", companyRepository.findAll());
        return "companies/companies";
    }

    @GetMapping("/companies")
    public String showCandidateListPaging(Model model,
                                          @RequestParam("page") Optional<Integer> page,
                                          @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<Company> candidatePage= companyService.findAll(
                currentPage - 1,pageSize,"id","asc");


        model.addAttribute("companyPage", candidatePage);

        int totalPages = candidatePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "companies/companies-paging";
    }
    @GetMapping("/show-add-form")
    public String showAddCompanyForm(Model model) {
        Company company = new Company();
        company.setAddress(new Address());
        model.addAttribute("company", company);
        model.addAttribute("countries",  CountryCode.values());
        return "companies/add-company";
    }

    @PostMapping("/add")
    public String addCompany(@ModelAttribute("company") Company company,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("countries", CountryCode.values());
            return "companies/add-company";
        }
        addressRepository.save(company.getAddress());
        companyRepository.save(company);
        return "redirect:/companies/list";
    }

    @GetMapping("/show-update-form/{id}")
    public ModelAndView edit(@PathVariable("id") long id) {
        ModelAndView mav = new ModelAndView("companies/update-companies");
        Optional<Company> opt = companyRepository.findById(id);
        if (opt.isPresent()) {
            Company company = opt.get();
            mav.addObject("company", company);
            mav.addObject("countries", CountryCode.values());
            mav.setViewName("companies/update-companies");
        }else {
            mav.setViewName("companies/companies");
        }
        return mav;
    }
    @PostMapping("/update")
    public String updateCompany(@ModelAttribute("company") Company company,
                                BindingResult result, Model model) {
        System.out.println("Company data: " + company); // Log thông tin nhận được
        System.out.println("Address data: " + company.getAddress());

        if (company.getAddress() == null || company.getAddress().getCountry() == null) {
            model.addAttribute("company", company);
            model.addAttribute("countries", CountryCode.values());
            return "companies/update-companies";
        }

        Address address = company.getAddress();
        if (address.getId() == null) {
            addressRepository.save(address);
        } else {
            addressRepository.save(address); // Lưu Address nếu đã tồn tại
        }

        company.setAddress(address);
        companyRepository.save(company); // Lưu Company với Address mới
        return "redirect:/companies/companies";
    }

    @GetMapping("/search")
    public String searchCompanies(
            @RequestParam("keyword") String keyword,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            Model model) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<Company> companyPage = companyService.searchCompanies(
                keyword, currentPage - 1, pageSize, "id", "asc");

        model.addAttribute("companyPage", companyPage);
        model.addAttribute("keyword", keyword);

        int totalPages = companyPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "companies/companies-paging";
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
        return "companies/view-company";
    }


}
