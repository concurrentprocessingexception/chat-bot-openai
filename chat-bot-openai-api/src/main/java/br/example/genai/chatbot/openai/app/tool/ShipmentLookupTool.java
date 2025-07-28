package br.example.genai.chatbot.openai.app.tool;

import br.example.genai.external.api.app.model.ApiResponse;
import br.example.genai.external.api.app.model.ShipmentDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentLookupTool {

    private final RestTemplate restTemplate;

    private final String shipmentApiUrl = "http://localhost:8081/api/shipment";

    @Tool(description = "Get shipment details by shipmentId from external shipment API")
    public String getShipmentDetails(String shipmentId) {
        try {
            var uri = UriComponentsBuilder
                    .fromHttpUrl(shipmentApiUrl)
                    .queryParam("shipmentId", shipmentId)
                    .toUriString();

            ApiResponse response = restTemplate.postForObject(uri, null, ApiResponse.class);
            ShipmentDetails d = response.getData();

            return String.format(
                    "Shipment %s: %s. Expected on %s. Delivered on %s. Charges: %s EUR. Fragile: %s",
                    d.getShipmentId(),
                    d.getShipmentDescription(),
                    d.getExpectedDeliveryDate(),
                    d.getActualDeliveryDate() != null ? d.getActualDeliveryDate() : "Not yet delivered",
                    d.getDeliveryCharges(),
                    d.isFragile() ? "Yes" : "No"
            );
        } catch (Exception e) {
            log.error("Error calling shipment API: {}", e.getMessage(), e);
            return "Shipment not found or failed to fetch.";
        }
    }
}
