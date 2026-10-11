package com.agri.ai.portal.controller;

import com.agri.ai.portal.dto.ReLoopReservationRequest;
import com.agri.ai.portal.dto.ReLoopCollectionRequest;
import com.agri.ai.portal.model.ReLoopListing;
import com.agri.ai.portal.repository.ReLoopListingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReLoopControllerTest {

    @Test
    void reservationSavesBuyerAndPickupDetailsForAnAvailableListing() {
        ReLoopListingRepository repository = mock(ReLoopListingRepository.class);
        ReLoopListing listing = new ReLoopListing();
        listing.setMaterialName("Paddy straw");
        listing.setMaterialType("Crop residue");
        listing.setQuantity(new java.math.BigDecimal("2"));
        listing.setUnit("quintal");
        listing.setDistrict("Thanjavur");
        listing.setSellerName("Farmer");
        listing.setDetails("");
        listing.setStatus("Available");
        listing.setStatus("Available");
        listing.setAvailableFrom(LocalDate.of(2026, 10, 10));
        when(repository.findById(7L)).thenReturn(Optional.of(listing));
        when(repository.save(listing)).thenReturn(listing);
        ReLoopController controller = new ReLoopController(repository);
        ReLoopReservationRequest request = new ReLoopReservationRequest();
        request.setReservedBy("Meena");
        request.setContactPhone("9876543210");
        request.setPickupDate(LocalDate.of(2026, 10, 12));

        controller.reserve(7L, request);

        assertEquals("Reserved", listing.getStatus());
        assertEquals("Meena", listing.getReservedBy());
        assertEquals("9876543210", listing.getReservationPhone());
        assertEquals(LocalDate.of(2026, 10, 12), listing.getPickupDate());
        verify(repository).save(listing);
    }

    @Test
    void reuseCatalogueIncludesReviewedSafetyCautions() {
        ReLoopController controller = new ReLoopController(mock(ReLoopListingRepository.class));

        assertFalse(controller.reuseCatalogue().isEmpty());
        assertEquals("Paddy straw", controller.reuseCatalogue().get(0).get("material"));
    }

    @Test
    void recordsPickupQuantityAndBothParticipantConfirmations() {
        ReLoopListingRepository repository = mock(ReLoopListingRepository.class);
        ReLoopListing listing = new ReLoopListing();
        listing.setMaterialName("Paddy straw");
        listing.setQuantity(new BigDecimal("2"));
        listing.setStatus("Reserved");
        when(repository.findById(7L)).thenReturn(Optional.of(listing));
        when(repository.save(listing)).thenReturn(listing);
        ReLoopCollectionRequest request = new ReLoopCollectionRequest();
        request.setCollectedQuantity(new BigDecimal("1.5"));
        request.setSellerConfirmation("Seller");
        request.setBuyerConfirmation("Buyer");

        var saved = new ReLoopController(repository).confirmCollection(7L, request);

        assertEquals("Collected", listing.getStatus());
        assertEquals(new BigDecimal("1.5"), listing.getCollectedQuantity());
        assertEquals("Seller", listing.getSellerPickupConfirmation());
        assertEquals("Buyer", listing.getBuyerPickupConfirmation());
        assertEquals("Seller", saved.get("sellerPickupConfirmation"));
        verify(repository).save(listing);
    }

    @Test
    void rejectsPickupQuantityAboveListedAmount() {
        ReLoopListingRepository repository = mock(ReLoopListingRepository.class);
        ReLoopListing listing = new ReLoopListing();
        listing.setQuantity(new BigDecimal("2"));
        listing.setStatus("Reserved");
        when(repository.findById(7L)).thenReturn(Optional.of(listing));
        ReLoopCollectionRequest request = new ReLoopCollectionRequest();
        request.setCollectedQuantity(new BigDecimal("2.1"));
        request.setSellerConfirmation("Seller");
        request.setBuyerConfirmation("Buyer");

        ResponseStatusException error = org.junit.jupiter.api.Assertions.assertThrows(
                ResponseStatusException.class,
                () -> new ReLoopController(repository).confirmCollection(7L, request));

        assertEquals(400, error.getStatusCode().value());
    }
}
