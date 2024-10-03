package org.example.demo.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.example.demo.dto.group.GroupCreate;
import org.example.demo.dto.group.GroupUpdate;
import org.example.demo.dto.product.request.ProductRequestDTO;
import org.example.demo.dto.product.response.ProductResponseDTO;
import org.example.demo.entity.Category;
import org.example.demo.entity.Product;
import org.example.demo.exporter.CategoryExcelExporter;
import org.example.demo.exporter.ProductExcelExporter;
import org.example.demo.mapper.product.response.ProductResponseMapper;
import org.example.demo.service.ProductService;
import org.example.demo.utils.PageableObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

@RestController
@RequestMapping(value = "product")
public class ProductController {
    @Autowired
    private ProductService productService;


    @GetMapping(value = "")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok((productService.findById(id)));
    }

    @RequestMapping(value = "overview")
    public ResponseEntity<Page<ProductResponseDTO>> findAllByPageV2(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "productCode", required = false) String productCode,
            @RequestParam(value = "categoryCode", required = false) String categoryCode,
            @RequestParam(value = "createdFrom", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createdFrom,
            @RequestParam(value = "createdTo", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate createdTo,
            @Valid @RequestBody PageableObject pageableObject,
            BindingResult bindingResult
    ) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return ResponseEntity.ok(productService.findAllOverviewByPage(name, productCode, categoryCode, createdFrom, createdTo, pageableObject));
    }

    @PostMapping(value = "", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductResponseDTO> create(@Validated(GroupCreate.class) @ModelAttribute ProductRequestDTO productRequestDTO, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return ResponseEntity.ok(productService.save(productRequestDTO));
    }

    @PutMapping(value = "{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id, @Validated(GroupUpdate.class) @ModelAttribute ProductRequestDTO productRequestDTO, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return ResponseEntity.ok(productService.update(id, productRequestDTO));
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<ProductResponseDTO> delete(@PathVariable Long id) {
        return ResponseEntity.ok(productService.softDelete(id));
    }

    @GetMapping("/export/excel")
    public void exportToExcel(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "productCode", required = false) String productCode,
            @RequestParam(value = "categoryCode", required = false) String categoryCode,
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

        List<ProductResponseDTO> listUsers = productService.findAllOverviewByPage(name, productCode, categoryCode, createdFrom, createdTo, null).getContent().stream().toList();

        ProductExcelExporter excelExporter = new ProductExcelExporter(listUsers);
        excelExporter.export(response);
    }
}
