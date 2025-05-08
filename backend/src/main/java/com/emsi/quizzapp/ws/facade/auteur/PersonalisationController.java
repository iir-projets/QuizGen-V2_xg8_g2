package com.emsi.quizzapp.ws.facade.auteur;

import com.emsi.quizzapp.service.facade.PersonalisationService;
import com.emsi.quizzapp.ws.dto.PersonalisationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personalisation")
public class PersonalisationController {

    private final PersonalisationService personalisationService;

    public PersonalisationController(PersonalisationService personalisationService) {
        this.personalisationService = personalisationService;
    }

    @PostMapping
    public ResponseEntity<PersonalisationDTO> save(@RequestBody PersonalisationDTO dto) {
        PersonalisationDTO saved = personalisationService.save(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonalisationDTO> findById(@PathVariable Long id) {
        PersonalisationDTO dto = personalisationService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping
    public ResponseEntity<PersonalisationDTO> update(@RequestBody PersonalisationDTO dto) {
        PersonalisationDTO updated = personalisationService.update(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        personalisationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}