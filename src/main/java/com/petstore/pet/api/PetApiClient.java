package com.petstore.pet.api;

import com.petstore.pet.dto.Pet;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PetApiClient {
    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    public Response addPet(Pet pet) {
        return given()
                .contentType(ContentType.JSON)
                .body(pet)
                .when()
                .post(BASE_URL + "/pet");
    }

    public Response getPetById(long petId) {
        return given()
                .when()
                .get(BASE_URL + "/pet/" + petId);
    }

    public Response updatePet(Pet pet) {
        return given()
                .contentType(ContentType.JSON)
                .body(pet)
                .when()
                .put(BASE_URL + "/pet");
    }

    public Response deletePet(long petId) {
        return given()
                .when()
                .delete(BASE_URL + "/pet/" + petId);
    }
}