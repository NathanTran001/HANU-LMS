//package fit.se2.hanulms.service;
//
//import fit.se2.hanulms.Repository.FacultyRepository;
//import fit.se2.hanulms.model.DTO.FacultyDTO;
//import fit.se2.hanulms.model.Faculty;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//@Service
//public class FacultyService {
//    @Autowired
//    private FacultyRepository facultyRepository;
//
//    public Page<FacultyDTO> getFacultiesWithCounts(Pageable pageable) {
//        return facultyRepository.findAllFacultyDTOs(pageable);
//    }
//}