
package com.developerportal.service;

import com.developerportal.entity.CatalogService;
import com.developerportal.enums.Lifecycle;
import com.developerportal.exception.ServiceNotFoundException;
import com.developerportal.repository.CatalogServiceRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceServiceTest {

    @Mock
    private CatalogServiceRepository repository;

    @InjectMocks
    private CatalogServiceService service;

    private CatalogService createSampleService() {
        CatalogService sample = new CatalogService();
        sample.setName("payment-service");
        sample.setDescription("Handles payments");
        sample.setRepositoryUrl(
                "https://github.com/example/payment-service"
        );
        sample.setOwner("payments-team");
        sample.setLifecycle(Lifecycle.PRODUCTION);
        return sample;
    }

    @Test
    void shouldCreateService() {
        CatalogService sample = createSampleService();

        when(repository.save(any(CatalogService.class)))
                .thenReturn(sample);

        CatalogService result = service.createService(sample);

        assertEquals("payment-service", result.getName());
        verify(repository).save(sample);
    }

    @Test
    void shouldReturnAllServices() {
        List<CatalogService> services =
                List.of(createSampleService());

        when(repository.findAll()).thenReturn(services);

        List<CatalogService> result = service.getAllServices();

        assertEquals(1, result.size());
        assertEquals("payment-service", result.get(0).getName());
    }

    @Test
    void shouldReturnServiceById() {
        UUID id = UUID.randomUUID();
        CatalogService sample = createSampleService();

        when(repository.findById(id))
                .thenReturn(Optional.of(sample));

        CatalogService result = service.getServiceById(id);

        assertEquals("payment-service", result.getName());
    }

    @Test
    void shouldThrowExceptionWhenServiceDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ServiceNotFoundException.class,
                () -> service.getServiceById(id)
        );
    }

    @Test
    void shouldUpdateExistingService() {
        UUID id = UUID.randomUUID();

        CatalogService existing = createSampleService();
        CatalogService updated = createSampleService();

        updated.setName("updated-payment-service");
        updated.setOwner("platform-team");
        updated.setLifecycle(Lifecycle.EXPERIMENTAL);

        when(repository.findById(id))
                .thenReturn(Optional.of(existing));

        when(repository.save(any(CatalogService.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CatalogService result = service.updateService(id, updated);

        assertEquals("updated-payment-service", result.getName());
        assertEquals("platform-team", result.getOwner());
        assertEquals(Lifecycle.EXPERIMENTAL, result.getLifecycle());

        verify(repository).save(existing);
    }

    @Test
    void shouldDeleteExistingService() {
        UUID id = UUID.randomUUID();

        when(repository.existsById(id)).thenReturn(true);

        service.deleteService(id);

        verify(repository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingMissingService() {
        UUID id = UUID.randomUUID();

        when(repository.existsById(id)).thenReturn(false);

        assertThrows(
                ServiceNotFoundException.class,
                () -> service.deleteService(id)
        );

        verify(repository, never()).deleteById(id);
    }
}
