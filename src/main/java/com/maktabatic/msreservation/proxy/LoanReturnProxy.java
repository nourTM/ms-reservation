package com.maktabatic.msreservation.proxy;

import com.maktabatic.msreservation.model.LoanReturn;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-query")
public interface LoanReturnProxy {

    @GetMapping("/query/loaned/{idNotice}")
    Long countLoaned(@PathVariable("idNotice") Long id);

    @GetMapping("/query/lastLoan")
    LoanReturn getLastLoan(@RequestParam("rr") String rr);
}
