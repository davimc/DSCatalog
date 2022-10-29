package com.davimc.DSCatalog.resources;

import com.davimc.DSCatalog.DTO.ProductDTO;
import com.davimc.DSCatalog.services.ProductService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {

    @Autowired
    private ProductService service;
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findAllPaged(
            @RequestParam(value = "categoryId", defaultValue = "0") Long CategoryId,
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy

    ) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage,
                Sort.Direction.valueOf(direction), orderBy);
        return ResponseEntity.ok().body(service.findAllPaged(CategoryId, name, pageRequest));
    }
    @GetMapping(value="/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }
    @PutMapping(value="/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        dto = service.update(id,dto);
        return ResponseEntity.ok().body(dto);
    }
    @DeleteMapping(value="/{id}")
    public ResponseEntity<ProductDTO> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
