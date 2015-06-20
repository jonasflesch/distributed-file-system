package br.feevale.distributeddatabase.web;

import br.feevale.distributeddatabase.model.Node;
import br.feevale.distributeddatabase.services.KnownNodesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

/**
 * Created by jonasflesch on 6/15/15.
 */
@RestController
public class BroadcastNodesController {

	@Autowired
	private KnownNodesService knownNodesService;

	@RequestMapping(value = "/knownNodes", method = RequestMethod.GET)
	public Set<Node> knownNodes(){
		return knownNodesService.listAll();
	}

	@RequestMapping(value = "/addNodes", method = RequestMethod.POST)
	public void knownNodes(@RequestBody Set<Node> nodes){
		knownNodesService.addNodes(nodes);
	}

	@Scheduled(fixedRate = 5000)
	public void broadcastNodes() {
		RestTemplate restTemplate = new RestTemplate();
		Set<Node> nodes = knownNodesService.listAll();
		for (Node node : nodes){
			restTemplate.postForLocation(node.getAddress()+"/addNodes", nodes);
		}
	}

}
