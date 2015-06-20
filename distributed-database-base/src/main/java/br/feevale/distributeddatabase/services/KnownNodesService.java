package br.feevale.distributeddatabase.services;

import br.feevale.distributeddatabase.config.DistributedDatabaseConfiguration;
import br.feevale.distributeddatabase.model.Node;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jonasflesch on 6/15/15.
 */
@Service
public class KnownNodesService implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {

	private static Log LOGGER = LogFactory.getLog(KnownNodesService.class);

	private Set<Node> nodes = new HashSet<>();

	private Node currentNode;

	private final DistributedDatabaseConfiguration distributedDatabaseConfiguration;

	@Inject
	public KnownNodesService(DistributedDatabaseConfiguration distributedDatabaseConfiguration){
		this.distributedDatabaseConfiguration = distributedDatabaseConfiguration;
	}

	public Set<Node> listAll(){
		return nodes;
	}

	public Set<Node> listAllExpectCurrent(){
		Set<Node> nodesExceptCurrent = new HashSet(nodes);
		nodesExceptCurrent.remove(currentNode);
		return nodesExceptCurrent;
	}

	public void addNodes(Set<Node> nodes){
		boolean changed = this.nodes.addAll(nodes);
		if(changed){
			LOGGER.info("Nodes have been added");
		}
	}

	@Override
	public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
		try {
			int port = event.getEmbeddedServletContainer().getPort();
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			currentNode = new Node();
			currentNode.setAddress("http://" + hostAddress + ":" + port);
			nodes.add(currentNode);

			Node nodeServer = new Node();
			nodeServer.setAddress(distributedDatabaseConfiguration.getServer());
			nodes.add(nodeServer);
		} catch (Exception e){
			LOGGER.error(e);
		}
	}


}
