package irs.server.irs_server.controllers;


import irs.server.irs_server.models.Section;
import irs.server.irs_server.models.User;
import irs.server.irs_server.payload.request.SectionRequest;
import irs.server.irs_server.payload.request.SectionUpdateRequest;
import irs.server.irs_server.payload.response.MessageResponse;
import irs.server.irs_server.payload.response.SectionsResponse;
import irs.server.irs_server.repository.SectionRepository;
import irs.server.irs_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/section")
public class SectionController {

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/getOrderSections")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<SectionsResponse> getOrderSections()
    {
        List<Section> sections = sectionRepository.findAllByOrderByOrder();
        SectionsResponse sectionsResponse = new SectionsResponse();
        sectionsResponse.setSectionList(sections);

        if (sections.size() > 0 ) {
            return new ResponseEntity<>(sectionsResponse, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/createSection")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSection(@Valid @RequestBody SectionRequest sectionRequest)
    {
        if (userRepository.existsById(sectionRequest.getUserId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User not found!"));
        }

        try {
            Optional<User> user = userRepository.findById(sectionRequest.getUserId());

            Section section = new Section(sectionRequest.getHeader(), sectionRequest.getBody(), sectionRequest.getVisible(), user.get());

            sectionRepository.save(section);
        }
        catch (Exception ex)
        {
            return ResponseEntity
                    .internalServerError()
                    .body("Error: " + ex.getMessage());
        }

        return  ResponseEntity.ok(new MessageResponse("Section created"));
    }

    @PostMapping("/updateSection")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSection(@Valid @RequestBody SectionUpdateRequest sectionUpdateRequest)
    {
        if (userRepository.existsById(sectionUpdateRequest.getUserId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User not found!"));
        }

        if (sectionRepository.existsById(sectionUpdateRequest.getId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Section not found!"));
        }

        try {
            Section section = sectionRepository.findById(sectionUpdateRequest.getId()).get();

            section.setHeader(sectionUpdateRequest.getHeader());
            section.setBody(sectionUpdateRequest.getBody());
            section.setVisible(sectionUpdateRequest.getVisible());

            User user = userRepository.findById(sectionUpdateRequest.getUserId()).get();

            section.setChangeBy(user);
            section.setChangeOn(Instant.now());

            sectionRepository.save(section);
        }
        catch (Exception ex)
        {
            return ResponseEntity
                    .internalServerError()
                    .body("Error: " + ex.getMessage());
        }

        return  ResponseEntity.ok(new MessageResponse("Section update"));
    }
}
