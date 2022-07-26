package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class VendorControllerTest {

  WebTestClient webTestClient;
  VendorRepository vendorRepository;
  VendorController controller;

  @Before
  public void setUp() throws Exception {
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
}
