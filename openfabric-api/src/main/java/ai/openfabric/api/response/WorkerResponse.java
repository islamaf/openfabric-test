package ai.openfabric.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class WorkerResponse {
    private String id;

    private String name;

    private String image;

    private String command;

    private String status;

    private String ports;
}