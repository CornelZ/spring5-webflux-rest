package guru.springframework.spring5webfluxrest.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

  @Test
  public void testUpdate() {
    BDDMockito //
        .given(categoryRepository.save(any(Category.class)))
        .willReturn(Mono.just(Category.builder().build()));

    Mono<Category> catToUpdateMono = Mono.just(Category.builder().description("Some Cat").build());

    webTestClient
        .put()
        .uri("/api/v1/categories/asdfasdf")
        .body(catToUpdateMono, Category.class)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  public void testPatchWithChanges() {
    BDDMockito //
        .given(categoryRepository.findById(anyString()))
        .willReturn(Mono.just(Category.builder().description("Some Description").build()));

    BDDMockito //
        .given(categoryRepository.save(any(Category.class)))
        .willReturn(Mono.just(Category.builder().build()));

    Mono<Category> catToUpdateMono =
        Mono.just(Category.builder().description("New Description").build());

    webTestClient
        .patch()
        .uri("/api/v1/categories/asdfasdf")
        .body(catToUpdateMono, Category.class)
        .exchange()
        .expectStatus()
        .isOk();

    verify(categoryRepository).save(any());
  }

  @Test
  public void testPatchNoChanges() {

    BDDMockito //
        .given(categoryRepository.findById(anyString()))
        .willReturn(Mono.just(Category.builder().description("Description").build()));

    BDDMockito //
        .given(categoryRepository.save(any(Category.class)))
        .willReturn(Mono.just(Category.builder().build()));

    Mono<Category> catToUpdateMono =
        Mono.just(Category.builder().description("Description").build());

    webTestClient
        .patch()
        .uri("/api/v1/categories/asdfasdf")
        .body(catToUpdateMono, Category.class)
        .exchange()
        .expectStatus()
        .isOk();

    verify(categoryRepository, never()).save(any());
  }
}
