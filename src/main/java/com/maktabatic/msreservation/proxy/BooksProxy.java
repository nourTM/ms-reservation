package com.maktabatic.msreservation.proxy;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "books-management")
public interface BooksProxy {
    @GetMapping("/exemplaireApi/nbbooks/{idNotice}")
    Long countExampTotal(@PathVariable("idNotice") Long id);

    @GetMapping("/exemplaireApi/idnotice/{rb}")
    Long getIdNotice(@PathVariable("rb") String rb);
}
