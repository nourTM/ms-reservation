package com.maktabatic.msreservation.proxy;

import com.maktabatic.msreservation.model.EmailTemplate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-notif")
public interface NotifProxy {
    @PostMapping("/v1/notification/textemail")
    String notify(@RequestBody EmailTemplate emailTemplate);
}
