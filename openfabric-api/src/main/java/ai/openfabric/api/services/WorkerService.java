package ai.openfabric.api.services;

import ai.openfabric.api.config.DockerConfig;
import ai.openfabric.api.model.Worker;
import ai.openfabric.api.repository.WorkerRepository;
import ai.openfabric.api.response.WorkerResponse;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.InvocationBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class WorkerService {
    private final WorkerRepository workerRepository;
    private final DockerConfig dockerConfig = new DockerConfig();
    private final DockerClient dockerClient = dockerConfig.establishConnection();

    public WorkerService (WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    private void addWorkersToDb() {
        List<Container> containers = dockerClient.listContainersCmd().exec();
        List<Worker> toSaveWorkers = new ArrayList<>();
        for (Container container: containers) {
            Worker existingWorker = this.getWorkerById(container.getId());
            if (existingWorker == null) {
                Worker worker = new Worker(container.getId(), container.getImage(), container.getCommand(), container.getNames(), container.getPorts());
                toSaveWorkers.add(worker);
            }
        }
        workerRepository.saveAll(toSaveWorkers);
    }

    public Page<WorkerResponse> getAllWorkers(int pageNumber, int pageSize) {
        this.addWorkersToDb();

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Worker> workers = workerRepository.findAll(pageable);
        List<WorkerResponse> workerResponses = new ArrayList<>();

        for (Worker worker: workers) {
            workerResponses.add(
                    WorkerResponse.builder()
                    .id(worker.id)
                    .name(Arrays.toString(worker.names))
                    .image(worker.image)
                    .ports(Arrays.toString(worker.ports))
                    .command(worker.command)
                    .status(worker.status)
                    .build()
            );
        }

        return new PageImpl<>(workerResponses, pageable, workers.getTotalElements());
    }

    public InspectContainerResponse getWorkerInformation(String id) {
        return dockerClient.inspectContainerCmd(id).exec();
    }

    public Statistics getWorkerStatistics(String id) {
        InvocationBuilder.AsyncResultCallback<Statistics> callback = new InvocationBuilder.AsyncResultCallback<>();
        dockerClient.statsCmd(id).exec(callback);
        Statistics stats;
        try {
            stats = callback.awaitResult();
            callback.close();
        } catch (RuntimeException | IOException e) {
            return null;
        }

        return stats;
    }

    public Worker getWorkerById(String id) {
        return workerRepository.findById(id).orElse(null);
    }

    public String startWorker(String id) {
        Worker worker = this.getWorkerById(id);
        dockerClient.startContainerCmd(worker.getId()).exec();

        InspectContainerResponse workerInfo = this.getWorkerInformation(id);
        boolean isRunning = Boolean.TRUE.equals(workerInfo.getState().getRunning());

        if (isRunning) {
            worker.setStatus("Running");
            workerRepository.save(worker);

            return "Worker " + worker.getId() + " has started running.";
        }

        return "Worker " + worker.getId() + " failed to start.";
    }

    public String stopWorker(String id) {
        Worker worker = this.getWorkerById(id);
        dockerClient.stopContainerCmd(worker.getId()).exec();

        InspectContainerResponse workerInfo = this.getWorkerInformation(id);
        boolean isNotRunning = Boolean.FALSE.equals(workerInfo.getState().getRunning());
        if (isNotRunning) {
            worker.setStatus("Exited");
            workerRepository.save(worker);

            return "Worker " + worker.getId() + " is now stopped.";
        }

        return "Worker " + worker.getId() + " failed to exit.";
    }
}
