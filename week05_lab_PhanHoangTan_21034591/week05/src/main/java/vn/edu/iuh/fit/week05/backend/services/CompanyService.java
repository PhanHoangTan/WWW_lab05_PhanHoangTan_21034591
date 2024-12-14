package vn.edu.iuh.fit.week05.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.week05.backend.models.Company;
import vn.edu.iuh.fit.week05.backend.repositories.CompanyRepository;

@Service
public class CompanyService {
    
    @Autowired
    private CompanyRepository companyRepository;
    public Page<Company> findAll(int pageNo, int pageSize, String sortBy,
                                 String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return companyRepository.findAll(pageable);//findFirst.../findTop...
    }
    public Page<Company> searchCompanies(String keyword, int pageNo, int pageSize, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return companyRepository.findByCompNameContainingOrPhoneContainingOrEmailContaining(
                keyword, pageable);
    }



}
