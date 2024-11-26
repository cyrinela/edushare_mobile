package Gs_Data.project.com.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "GsUser", url = "http://localhost:8085")
public interface UserFeignClient {
    @GetMapping("/users/{id}")
    Map<String, Object> getUserById(@PathVariable("id") Long id);
}
