package com.cogent.authservice.constants;

public class MicroServiceConstants {
    public static final String LOGIN_MICROSERVICE = "/login-service/api/login";
    public static final String LOGIN_MICROSERVICE_INDEX = "/login-service/api/index";
    public static final String DOCTOR_MICROSERVICE = "/doctor-service"; // doctor-service
    public static final String PATIENT_MICROSERVICE = "/patient-service";
    public static final String DB_SERVICE = "db-producer";
    public static final String USER_ENDPOINT = "/api/user";

    public interface UserMicroServiceConstants {
        String FETCH_USER_BY_USERNAME = "/get/{username}";
    }

    public interface DoctorMicroServiceConstants{
        String GET_DOCTOR_BY_USERNAME=DOCTOR_MICROSERVICE + "/{username}";
    }
}
