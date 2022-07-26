package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class CategoryController {
  private final CategoryRepository categoryRepository;

  public CategoryController(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @GetMapping("/api/v1/categories")
  public Flux<Category> list() {
    return categoryRepository.findAll();
  }

  @GetMapping("/api/v1/categories/{id}")
  public Mono<Category> getById(@PathVariable String id) {
    log.debug("getById id: {}", id);
    return categoryRepository.findById(id);
  }
}
