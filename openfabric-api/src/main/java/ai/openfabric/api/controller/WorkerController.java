package ai.openfabric.api.controller;

import ai.openfabric.api.response.WorkerResponse;
import ai.openfabric.api.services.WorkerService;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Statistics;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${node.api.path}/worker")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @PostMapping(path = "/hello")
    public @ResponseBody String hello(@RequestBody String name) {
        return "Hello!" + name;
    }

    @GetMapping(path ="/all")
    @ResponseStatus(HttpStatus.OK)
    public Page<WorkerResponse> getAllWorkers(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
        return workerService.getAllWorkers(pageNumber, pageSize);
    }

    @PostMapping(path = "/start")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String startWorker(@RequestBody String id) {
        return workerService.startWorker(id);
    }

    @PostMapping(path = "/stop")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String stopWorker(@RequestBody String id) {
        return workerService.stopWorker(id);
    }

    @GetMapping(path = "/information")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public InspectContainerResponse getWorkerInformation(@RequestParam String id) {
        return workerService.getWorkerInformation(id);
    }

    @GetMapping(path = "/statistics")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Statistics getWorkerStatistics(@RequestParam String id) {
        return workerService.getWorkerStatistics(id);
    }

}
