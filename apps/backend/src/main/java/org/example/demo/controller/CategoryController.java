package org.example.demo.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.apache.coyote.BadRequestException;
import org.example.demo.dto.category.request.CategoryRequestDTO;
import org.example.demo.dto.category.response.CategoryResponseDTO;
import org.example.demo.dto.group.GroupCreate;
import org.example.demo.dto.group.GroupUpdate;
import org.example.demo.entity.Category;
import org.example.demo.enums.EStatus;
import org.example.demo.exporter.CategoryExcelExporter;
import org.example.demo.mapper.category.response.CategoryResponseMapper;
import org.example.demo.mapper.product.response.ProductResponseMapper;
import org.example.demo.service.CategoryService;
import org.example.demo.service.ProductService;
import org.example.demo.utils.PageableObject;
import org.example.demo.validate.NoSpecialCharacterConstraint;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    @RequestMapping(value = "overview")
    public ResponseEntity<Page<CategoryResponseDTO>> findAllByPageV2(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "createdFrom", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createdFrom,
            @RequestParam(value = "createdTo", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createdTo,
            @Valid @RequestBody PageableObject pageableObject,
            BindingResult bindingResult
    ) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        String query = pageableObject.getQuery();
        return ResponseEntity.ok(categoryService.findAllOverviewByPage(name, code, createdFrom, createdTo, pageableObject));
    }

    @GetMapping(value = "")
    public ResponseEntity<?> findAll() {
        Sort sort = Sort.by(Sort.Direction.fromString("asc"), "categoryCode");
        return ResponseEntity.ok(categoryService.findAllSortCode(sort));
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.softDelete(id));
    }

    @PostMapping(value = "", consumes = {"multipart/form-data"})
    public ResponseEntity<?> create(@Validated(GroupCreate.class) @ModelAttribute CategoryRequestDTO categoryRequestDTO, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return ResponseEntity.ok(categoryService.save(categoryRequestDTO));
    }

    @PutMapping(value = "{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> update(@PathVariable Long id, @Validated(GroupUpdate.class) @ModelAttribute CategoryRequestDTO categoryRequestDTO, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return ResponseEntity.ok(categoryService.update(id, categoryRequestDTO));
    }

    @GetMapping("/export/excel")
    public void exportToExcel(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "createdFrom", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createdFrom,
            @RequestParam(value = "createdTo", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createdTo,
            HttpServletResponse response
    ) {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<CategoryResponseDTO> listUsers = categoryService.findAllOverviewByPage(name, code, createdFrom, createdTo, null).getContent();

        CategoryExcelExporter excelExporter = new CategoryExcelExporter(listUsers);
        excelExporter.export(response);
    }
}