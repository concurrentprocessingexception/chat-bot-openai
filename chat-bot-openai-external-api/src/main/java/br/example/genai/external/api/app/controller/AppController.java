package br.example.genai.external.api.app;

import br.example.genai.external.api.app.model.ApiResponse;
import br.example.genai.external.api.app.model.ShipmentDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class AppController {

    // Static predefined list of 10 shipments
    private static final List<ShipmentDetails> SHIPMENT_DATA = Arrays.asList(
            new ShipmentDetails("SHIP001", "Electronics", "2025-08-01", "2025-07-27", "20.50", "DELIVERED" , true),
            new ShipmentDetails("SHIP002", "Books", "2025-08-02", "2025-07-28", "5.00", "DELAYED" , false),
            new ShipmentDetails("SHIP003", "Furniture", "2025-08-03", null, "100.00", "ON TIME" , true),
            new ShipmentDetails("SHIP004", "Clothes", "2025-08-04", null, "15.00", "ON TIME" , false),
            new ShipmentDetails("SHIP005", "Shoes", "2025-08-05", null, "12.00", "ON TIME" , false),
            new ShipmentDetails("SHIP006", "Toys", "2025-08-06", null, "8.00", "ON TIME" , false),
            new ShipmentDetails("SHIP007", "Medicines", "2025-08-07", null, "10.00", "ON TIME" , true),
            new ShipmentDetails("SHIP008", "Kitchenware", "2025-08-08", null, "18.00", "ON TIME" , false),
            new ShipmentDetails("SHIP009", "Groceries", "2025-08-09", null, "7.50", "ON TIME" , false),
            new ShipmentDetails("SHIP010", "Laptop", "2025-08-10", null, "30.00", "ON TIME" , true)
    );

    @PostMapping("/api/shipment")
    public ResponseEntity<ApiResponse> handleRequest(@RequestParam String shipmentId) {
        log.info("Request Received for shipmentId: {}", shipmentId);
        ApiResponse apiResponse;

        try {
            apiResponse = createApiResponse(shipmentId);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            log.error("Error occurred while processing shipmentId {}: {}", shipmentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("FAILURE", "Invalid request: " + e.getMessage(), null));
        } finally {
            log.info("Request processing complete for shipmentId: {}", shipmentId);
        }
    }

    private ApiResponse createApiResponse(String shipmentId) {
        Optional<ShipmentDetails> match = SHIPMENT_DATA.stream()
                .filter(s -> s.getShipmentId().equalsIgnoreCase(shipmentId))
                .findFirst();

        if (match.isPresent()) {
            return new ApiResponse("SUCCESS", "Shipment details found", match.get());
        } else {
            throw new IllegalArgumentException("Shipment ID not found in predefined list.");
        }
    }
}
