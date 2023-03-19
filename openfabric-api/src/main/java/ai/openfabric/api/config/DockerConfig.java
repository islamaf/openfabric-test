package ai.openfabric.api.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {
    public DockerClient establishConnection(){
        DefaultDockerClientConfig connection
                = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerTlsVerify("1")
                .withDockerHost("tcp://localhost:2375")
                .build();

        return  DockerClientBuilder.getInstance(connection).build();
    }
}