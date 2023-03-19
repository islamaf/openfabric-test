package ai.openfabric.api.model;


import com.github.dockerjava.api.model.ContainerPort;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity()
public class Worker extends Datable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
    @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")
    @Getter
    @Setter
    public String id;

    public String image;

    public String command;

    public String[] names;

    @Setter
    public String status;

    public ContainerPort[] ports;

    public Worker(String id, String image, String command, String[] names, ContainerPort[] ports) {
        super();
    }
}
