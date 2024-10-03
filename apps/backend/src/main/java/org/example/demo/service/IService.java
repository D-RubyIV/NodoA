package org.example.demo.service;

import org.apache.coyote.BadRequestException;

import java.io.IOException;
import java.util.List;
/**
 * @param <E> Entity
 * @param <ID> Kiểu dữ liệu ID của Entity
 * @param <RQ> Đối tượng requestDTO
 **/
public interface IService<E, ID, RQ, RP> {
    List<RP> findAll();
    RP findById(ID id) throws BadRequestException;
    RP softDelete(ID id) throws BadRequestException;
    RP save(RQ requestDTO) throws IOException;
    RP update(ID id, RQ requestDTO) throws BadRequestException;
}