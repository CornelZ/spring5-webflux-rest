package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CategoryControllerTest {

  WebTestClient webTestClient;
  CategoryRepository categoryRepository;
  CategoryController categoryController;

  @Before
  public void setUp() {
    categoryRepository = Mockito.mock(CategoryRepository.class);
    categoryController = new CategoryController(categoryRepository);
    webTestClient = WebTestClient.bindToController(categoryController).build();
  }

  @Test
  public void list() {
    BDDMockito.given(categoryRepository.findAll())
        .willReturn(
            Flux.just(
                Category.builder().description("Cat1").build(),
                Category.builder().description("Cat2").build()));

    webTestClient
        .get()
        .uri("/api/v1/categories/")
        .exchange()
        .expectBodyList(Category.class)
        .hasSize(2);
  }

  @Test
  public void getById() {
    BDDMockito.given(categoryRepository.findById("someid"))
        .willReturn(Mono.just(Category.builder().description("Cat").build()));

    webTestClient.get().uri("/api/v1/categories/someid").exchange().expectBody(Category.class);
  }

  @Test
  public void testCreateCateogry() {

    final Category category = Category.builder().description("descrp").build();

    BDDMockito //
        .given(categoryRepository.saveAll(ArgumentMatchers.<Publisher<Category>>any())) //
        .willReturn(Flux.just(category));

    Mono<Category> catToSaveMono = Mono.just(Category.builder().description("Some Cat").build());

    webTestClient
        .post()
        .uri("/api/v1/categories")
        .body(catToSaveMono, Category.class)
        .exchange()
        .expectStatus()
        .isCreated();
  }
}
