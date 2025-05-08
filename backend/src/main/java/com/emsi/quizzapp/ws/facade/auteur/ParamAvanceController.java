package com.emsi.quizzapp.ws.facade.auteur;

import com.emsi.quizzapp.service.facade.ParamAvanceService;
import com.emsi.quizzapp.ws.dto.ParamAvanceDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/param-avance")
public class ParamAvanceController {

    private final ParamAvanceService paramAvanceService;

    public ParamAvanceController(ParamAvanceService paramAvanceService) {
        this.paramAvanceService = paramAvanceService;
    }

    @PostMapping
    public ResponseEntity<ParamAvanceDTO> save(@RequestBody ParamAvanceDTO dto) {
        ParamAvanceDTO saved = paramAvanceService.save(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParamAvanceDTO> findById(@PathVariable Long id) {
        ParamAvanceDTO dto = paramAvanceService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<ParamAvanceDTO> findByQuizId(@PathVariable Long quizId) {
        ParamAvanceDTO dto = paramAvanceService.findByQuizId(quizId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping
    public ResponseEntity<ParamAvanceDTO> update(@RequestBody ParamAvanceDTO dto) {
        ParamAvanceDTO updated = paramAvanceService.update(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        paramAvanceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}