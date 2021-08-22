package com.maktabatic.msreservation.proxy;

import com.maktabatic.msreservation.model.Reader;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name="account-management")
public interface ReaderProxy {
    @GetMapping("/api/readers/{id}")
    Reader verifyRFIDReader(@PathVariable("id") String id, @RequestParam("projection") String projection);
}
