package br.feevale.distributeddatabase.services;

import br.feevale.distributeddatabase.model.Node;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jonasflesch on 6/15/15.
 */
@Service
public class BroadcastFileService {

	private static Log LOGGER = LogFactory.getLog(BroadcastFileService.class);

	@Autowired
	private KnownNodesService knownNodesService;

	private Set<String> pathsCreated = new HashSet<>();
	private Set<String> pathsDeleted = new HashSet<>();

	public void addPathCreated(String path){
		pathsCreated.add(path);
	}

	public void addPathDeleted(String path){
		pathsDeleted.add(path);
	}

	public void createFile(final String path, final File file){
		if(pathsCreated.remove(path)){
			return;
		}

		CloseableHttpClient httpClient = HttpClients.createDefault();

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("path", path, ContentType.TEXT_PLAIN);
		builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
		HttpEntity multipart = builder.build();

		for (Node node : knownNodesService.listAllExpectCurrent()){
			try {
				HttpPost uploadFile = new HttpPost(node.getAddress() + "/createFile");
				uploadFile.setEntity(multipart);
				httpClient.execute(uploadFile);

				LOGGER.info("Arquivo " + file.getName() + " enviado com sucesso para o nodo " + node);
			} catch (Exception e){
				LOGGER.error(e);
			}

		}
	}

	public void deleteFile(final String path){
		if(pathsDeleted.remove(path)){
			return;
		}

		CloseableHttpClient httpClient = HttpClients.createDefault();

		List<NameValuePair> nvps = new ArrayList<>();
		nvps.add(new BasicNameValuePair("path", path));

		for (Node node : knownNodesService.listAllExpectCurrent()){
			try {
				HttpPost uploadFile = new HttpPost(node.getAddress() + "/deleteFile");
				uploadFile.setEntity(new UrlEncodedFormEntity(nvps));
				httpClient.execute(uploadFile);

				LOGGER.info("Arquivo " + path + " apagado com sucesso no nodo " + node);
			} catch (Exception e){
				LOGGER.error(e);
			}

		}
	}

}
