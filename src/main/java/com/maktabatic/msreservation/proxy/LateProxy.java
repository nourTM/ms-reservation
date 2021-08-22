package com.maktabatic.msreservation.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-retard")
public interface LateProxy {
    @GetMapping("/api/punished")
    boolean isPunished(@RequestParam("rr") String rr);
    @PostMapping("/api/punish")
    boolean punish(@RequestParam("rr") String rr,@RequestParam("rb") String rb);
}
