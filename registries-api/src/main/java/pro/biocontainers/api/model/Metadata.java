package pro.biocontainers.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Describes this registry to better allow for mirroring and indexing.
 */
@Data
public class Metadata {
    @JsonProperty("version")
    private String version;

    @JsonProperty("api_version")
    private String apiVersion;

    @JsonProperty("country")
    private String country;

    @JsonProperty("friendly_name")
    private String friendlyName;
}
