package guru.springframework.spring5webfluxrest.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class VendorControllerTest {

  WebTestClient webTestClient;
  VendorRepository vendorRepository;
  VendorController controller;

  @Before
  public void setUp() {
    vendorRepository = Mockito.mock(VendorRepository.class);
    controller = new VendorController(vendorRepository);
    webTestClient = WebTestClient.bindToController(controller).build();
  }

  @Test
  public void list() {

    final Vendor vendor1 = Vendor.builder().firstName("Fred").lastName("Flintstone").build();
    final Vendor vendor2 = Vendor.builder().firstName("Barney").lastName("Rubble").build();

    BDDMockito //
        .given(vendorRepository.findAll()) //
        .willReturn(Flux.just(vendor1, vendor2));

    webTestClient.get().uri("/api/v1/vendors").exchange().expectBodyList(Vendor.class).hasSize(2);
  }

  @Test
  public void getById() {
    final Vendor vendor = Vendor.builder().firstName("Jimmy").lastName("Johns").build();

    BDDMockito //
        .given(vendorRepository.findById("someid")) //
        .willReturn(Mono.just(vendor));

    webTestClient.get().uri("/api/v1/vendors/someid").exchange().expectBody(Vendor.class);
  }

  @Test
  public void testCreateVendor() {
    BDDMockito //
        .given(vendorRepository.saveAll(ArgumentMatchers.<Publisher<Vendor>>any()))
        .willReturn(Flux.just(Vendor.builder().build()));

    final Vendor vendor = Vendor.builder().firstName("First Name").lastName("Last Name").build();
    Mono<Vendor> vendorToSaveMono = Mono.just(vendor);

    webTestClient
        .post()
        .uri("/api/v1/vendors")
        .body(vendorToSaveMono, Vendor.class)
        .exchange()
        .expectStatus()
        .isCreated();
  }

  @Test
  public void testUpdateVendor() {

    BDDMockito //
        .given(vendorRepository.save(ArgumentMatchers.<Vendor>any()))
        .willReturn(Mono.just(Vendor.builder().build()));

    Mono<Vendor> vendorMonoToUpdate = Mono.just(Vendor.builder().build());

    webTestClient
        .put()
        .uri("/api/v1/vendors/someid")
        .body(vendorMonoToUpdate, Vendor.class)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  public void testPatchVendorWithChanges() {

    BDDMockito //
        .given(vendorRepository.findById(anyString()))
        .willReturn(Mono.just(Vendor.builder().firstName("Jimmy").build()));

    BDDMockito //
        .given(vendorRepository.save(any(Vendor.class)))
        .willReturn(Mono.just(Vendor.builder().build()));

    Mono<Vendor> vendorMonoToUpdate = Mono.just(Vendor.builder().firstName("Jim").build());

    webTestClient
        .patch()
        .uri("/api/v1/vendors/someid")
        .body(vendorMonoToUpdate, Vendor.class)
        .exchange()
        .expectStatus()
        .isOk();

    verify(vendorRepository).save(any());
  }

  @Test
  public void testPatchVendorWithoutChanges() {

    BDDMockito //
        .given(vendorRepository.findById(anyString()))
        .willReturn(Mono.just(Vendor.builder().firstName("Jimmy").build()));

    BDDMockito //
        .given(vendorRepository.save(any(Vendor.class)))
        .willReturn(Mono.just(Vendor.builder().build()));

    Mono<Vendor> vendorMonoToUpdate = Mono.just(Vendor.builder().firstName("Jimmy").build());

    webTestClient
        .patch()
        .uri("/api/v1/vendors/someid")
        .body(vendorMonoToUpdate, Vendor.class)
        .exchange()
        .expectStatus()
        .isOk();

    verify(vendorRepository, never()).save(any());
  }
}
