package com.cogent.authservice.feignInterface;

import com.cogent.authservice.constants.MicroServiceConstants;
import com.cogent.authservice.responseDTO.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@FeignClient(name = MicroServiceConstants.DB_SERVICE)
@Service
@RequestMapping(value = MicroServiceConstants.USER_ENDPOINT)
public interface UserInterface {
    @RequestMapping(value = MicroServiceConstants.UserMicroServiceConstants.FETCH_USER_BY_USERNAME)
    Optional<UserResponseDTO> fetchUserByUsername(@PathVariable("username") String username);
}
