package fit.se2.hanulms.controller;

import fit.se2.hanulms.model.DTO.FacultyDTO;
import fit.se2.hanulms.model.Faculty;
import fit.se2.hanulms.repository.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/faculties")
public class FacultyController {
    @Autowired
    private FacultyRepository facultyRepository;

    @GetMapping("/{code}")
    public ResponseEntity<?> getFaculty(@PathVariable String code) {
        Optional<Faculty> existing = facultyRepository.findById(code);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Faculty not found"));
        }

        return ResponseEntity.ok((new FacultyDTO(existing.get())));
    }

    @GetMapping("/all")
    public ResponseEntity<List<FacultyDTO>> listFacultyAll() {
        return ResponseEntity.ok(facultyRepository.findAll().stream().map(
                FacultyDTO::new
        ).collect(Collectors.toList()));
    }
}
