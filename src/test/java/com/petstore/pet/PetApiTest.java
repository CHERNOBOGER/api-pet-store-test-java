package com.petstore.pet;

import com.petstore.pet.api.PetApiClient;
import com.petstore.pet.dto.Pet;
import com.petstore.pet.dto.Category;
import com.petstore.pet.dto.Tag;
import com.petstore.utils.LoggingExtension;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@ExtendWith(LoggingExtension.class)
class PetApiTest {
    private PetApiClient petApiClient;
    //я конечно мог поискать ещё смешных коровок но я думаю и так понятно
    private String PhotoCow = "https://i.pinimg.com/736x/ce/08/f4/ce08f41435d66ef6571bbc43105b432a.jpg";
    private static final Logger logger = LoggerFactory.getLogger(PetApiTest.class);

    @BeforeEach
    void setUp() {
        petApiClient = new PetApiClient();
    }

    @Test
    void addManuallyPet() {
        Pet testPet = createTestPet(2001L, "Burenka",
                "Cows", 1L,1L, "Funnies",
                Arrays.asList(PhotoCow),
                "available");
        logger.debug("Создан питомец: {}", testPet);

        Response response = petApiClient.addPet(testPet);
        TestingTest(response,200);

        logger.info("Проверяем коровку...");
        Pet createdPet = response.as(Pet.class);
        assertEquals(testPet.getId(), createdPet.getId());
        assertEquals(testPet.getName(), createdPet.getName());
        assertEquals("Cows", createdPet.getCategory().getName());
        assertTrue(createdPet.getPhotoUrls().contains(PhotoCow));
        logger.info("Всё в норме ^_^");

        ClearPet(testPet.getId());
    }

    @Test
    void getPetById() {
        Pet testPet = createSimplePet(2002L, "Murka");
        petApiClient.addPet(testPet);
        logger.debug("Создан питомец: {}", testPet);

        Response response = petApiClient.getPetById(testPet.getId());
        TestingTest(response,200);

        logger.info("Проверяем коровку...");
        assertEquals("Murka", response.as(Pet.class).getName());
        logger.info("Всё в норме ^_^");

        ClearPet(testPet.getId());

    }

    @Test
    void updatePet() {
        Pet testPet = createSimplePet(2003L, "OldName");
        petApiClient.addPet(testPet);
        logger.info("Продаём коровку...");
        testPet.setName("NewName");
        testPet.setStatus("sold");

        Response response = petApiClient.updatePet(testPet);
        TestingTest(response,200);

        logger.info("Проверяем коровку...");
        assertEquals("NewName", response.as(Pet.class).getName());
        logger.info("Всё в норме ^_^");

        ClearPet(testPet.getId());

    }

    @Test
    void deleteAndGet_NonExistentPet() {
        logger.info("Удаляем несуществующюю коровку");
        Response deleteresponse = petApiClient.deletePet(-999999L);
        TestingTest(deleteresponse,404);
        logger.info("Не удалили(");
        logger.info("Ищем несуществующюю коровку");
        Response getresponse = petApiClient.getPetById(-999999L);
        TestingTest(getresponse,404);
        logger.info("Не нашли(");
    }

    @Test
    void addEmptyNamePet() {
        logger.info("Создаём безымянную коровку");
        Pet invalidPet = createSimplePet(4051L, ""); // Пустое имя
        Response response = petApiClient.addPet(invalidPet);
        logger.debug("Код ответа: {}, Тело: {}", response.getStatusCode(), response.getBody().asString());
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 405);

        if (response.getStatusCode() == 200) {
            ClearPet(invalidPet.getId());
        }
    }

    @Test
    void addMinimalValidDataPet() {
        Pet minimalPet = new Pet();
        minimalPet.setId(2004L);
        minimalPet.setName("Minicow(calf)");
        logger.debug("Создан минипитомец: {}", minimalPet);

        Response response = petApiClient.addPet(minimalPet);
        TestingTest(response,200);

        ClearPet(minimalPet.getId());

    }

    @Test
    void addLongNamePet() {
        Pet longNamePet = createSimplePet(4052L, "A".repeat(1000));
        Response response = petApiClient.addPet(longNamePet);
        logger.debug("Код ответа: {}, Тело: {}", response.getStatusCode(), response.getBody().asString());
        assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 405);

        if (response.getStatusCode() == 200) {
            ClearPet(longNamePet.getId());
        }
    }

    private void TestingTest(Response response, int status){
        if (response.getStatusCode() != status) {
            logger.error("Ошибка! Код ответа: {}, Тело: {}",
                    response.getStatusCode(), response.getBody().asString());
            fail("Ожидался статус " + status + ", но получен " + response.getStatusCode());
        }
    }

    private void ClearPet(Long petId) {
        try {
            petApiClient.deletePet(petId);
            logger.info("Коровка успешно удалена");
        } catch (Exception e) {
            logger.warn("Не удалось удалить коровку {}: {}", petId, e.getMessage());
        }
    }

    private Pet createSimplePet(Long id, String name) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        return pet;
    }
    // оставил если понадобится
    private Pet createSemiAutoTestPet(Long categoryId, String categoryName,
                                      Long tagId, String tagName,
                                      String status) {

        Pet pet = new Pet();
                 //это убирает отрицательные числа и ограничивет до миллиона
        pet.setId(Math.abs(new Random().nextLong() % 1000000L));
        pet.setName("TestPet_" + new String(String.valueOf(new Random().nextInt())));

        Category category = new Category();
        // категории я рукописными, но если что:
//        category.setId(0L);
//        category.setName("TestPets");
        category.setId(categoryId);
        category.setName(categoryName);
        pet.setCategory(category);

        pet.setPhotoUrls(Arrays.asList(PhotoCow));

        Tag tag = new Tag();
        tag.setId(tagId);
        tag.setName(tagName);
        pet.setTags(Arrays.asList(tag));

        pet.setStatus(status);

        return pet;
    }

    private Pet createTestPet(Long id, String name,
                              String categoryName, Long categoryId,
                              Long tagId, String tagName,
                              List<String> photoUrls, String status) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        pet.setCategory(category);

        Tag tag = new Tag();
        tag.setId(tagId);
        tag.setName(tagName);
        pet.setTags(Arrays.asList(tag));

        pet.setPhotoUrls(photoUrls);
        pet.setStatus(status);
        return pet;
    }
}