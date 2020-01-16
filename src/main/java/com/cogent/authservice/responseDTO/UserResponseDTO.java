package com.cogent.authservice.responseDTO;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author smriti on 6/27/19
 */

@Getter
@Setter
public class UserResponseDTO implements Serializable{

    private Long id;
    private String password;
    private Integer loginAttempt;
    private Date createdAt;
    private Date updatedAt;
    private String username;
    private String name;
    private String medicalSpeciality;
    private String roles;
    private int active;
    private int isGp;

    private List<String> rolesList = new ArrayList<>();

}
