package org.example.demo.service;

import org.apache.coyote.BadRequestException;

import java.io.IOException;
import java.util.List;

/**
 * @param <E> Entity
 * @param <ID> Kiểu dữ liệu ID của Entity
 * @param <RQ> Đối tượng requestDTO
 **/
public interface IService<E, ID, RQ> {
    List<E> findAll();
    E findById(ID id) throws BadRequestException;
    E softDelete(ID id) throws BadRequestException;
    E save(RQ requestDTO) throws IOException;
    E update(ID id, RQ requestDTO) throws BadRequestException;
}