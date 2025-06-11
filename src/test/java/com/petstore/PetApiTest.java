package com.petstore;

import com.petstore.api.PetApiClient;
import com.petstore.dto.Pet;
import com.petstore.dto.Category;
import com.petstore.dto.Tag;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PetApiTest {
    private PetApiClient petApiClient;
    private Pet testPet;

    @BeforeEach
    void setUp() {
        petApiClient = new PetApiClient();

        testPet = new Pet();
        testPet.setId(21345L);
        testPet.setName("Cow");

        Category category = new Category();
        category.setId(1L);
        category.setName("Cows");
        testPet.setCategory(category);
                                         // просто смешная коровка)
        testPet.setPhotoUrls(Arrays.asList("https://i.pinimg.com/736x/ce/08/f4/ce08f41435d66ef6571bbc43105b432a.jpg"));

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("funnies");
        testPet.setTags(Arrays.asList(tag));

        testPet.setStatus("available");
    }

    @Test
    @Order(1)
    void testAddPet() {
        Response response = petApiClient.addPet(testPet);

        assertEquals(200, response.getStatusCode());

        Pet createdPet = response.as(Pet.class);
        assertEquals(testPet.getId(), createdPet.getId());
        assertEquals(testPet.getName(), createdPet.getName());
    }

    @Test
    @Order(2)
    void testGetPetById() {
        petApiClient.addPet(testPet);

        Response response = petApiClient.getPetById(testPet.getId());

        assertEquals(200, response.getStatusCode());

        Pet retrievedPet = response.as(Pet.class);
        assertEquals(testPet.getId(), retrievedPet.getId());
        assertEquals(testPet.getName(), retrievedPet.getName());
    }

    @Test
    @Order(3)
    void testUpdatePet() {
        petApiClient.addPet(testPet);
        testPet.setName("Updated Cow");
        testPet.setStatus("sold");

        Response response = petApiClient.updatePet(testPet);

        assertEquals(200, response.getStatusCode());

        Pet updatedPet = response.as(Pet.class);
        assertEquals("Updated Cow", updatedPet.getName());
        assertEquals("sold", updatedPet.getStatus());
    }

    @Test
    @Order(4)
    void testDeletePet() {
        petApiClient.addPet(testPet);

        Response response = petApiClient.deletePet(testPet.getId());

        assertEquals(200, response.getStatusCode());

        Response getResponse = petApiClient.getPetById(testPet.getId());
        assertEquals(404, getResponse.getStatusCode());
    }

    @AfterEach
    void cleanUp() {
        try {
            petApiClient.deletePet(testPet.getId());
        } catch (Exception e) {
        }
    }
}