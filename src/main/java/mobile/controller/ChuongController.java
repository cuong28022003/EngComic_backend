package mobile.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mobile.model.Entity.*;
import mobile.repository.*;
import java.util.List;

@RestController
@RequestMapping("/api/chuong")
public class ChuongController {

    @Autowired
    private ChuongRepository chuongRepository;

    @PostMapping
    public ResponseEntity<?> luuChuong(@RequestBody Chuong chuong) {
        Chuong savedChuong = chuongRepository.save(chuong);
        return ResponseEntity.ok(savedChuong);
    }

    // @GetMapping("/{id}")
    // public ResponseEntity<Object> layChuongTheoId(@PathVariable String id) {
    // return chuongRepository.findById(id)
    // .map(chuong -> ResponseEntity.ok(chuong))
    // .orElse(ResponseEntity.status(404).body("Không tìm thấy chương!"));
    // }
    @GetMapping("/{id}")
    public ResponseEntity<Chuong> getChuongById(@PathVariable String id) {
        return chuongRepository.findById(id) // Đúng tên phương thức trong MongoRepository
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
