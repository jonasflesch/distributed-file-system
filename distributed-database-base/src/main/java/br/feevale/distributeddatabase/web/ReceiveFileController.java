package br.feevale.distributeddatabase.web;

import br.feevale.distributeddatabase.config.DistributedDatabaseConfiguration;
import br.feevale.distributeddatabase.services.BroadcastFileService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by jonasflesch on 6/20/15.
 */
@RestController
public class ReceiveFileController {

	private static Log LOGGER = LogFactory.getLog(ReceiveFileController.class);

	@Inject
	private DistributedDatabaseConfiguration distributedDatabaseConfiguration;

	@Inject
	private BroadcastFileService broadcastFileService;

	@RequestMapping(value="/createFile", method= RequestMethod.POST)
	public void handleFileUpload(@RequestParam("path") String path,
								 @RequestParam("file") MultipartFile file){
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(distributedDatabaseConfiguration.getFolder() + path)));
				stream.write(bytes);
				stream.close();

				broadcastFileService.addPathCreated(path);

				LOGGER.info("Arquivo " + path + " gravado com sucesso");
			} catch (Exception e) {
				LOGGER.info("Ocorreu um problema ao realizar o upload de " + path, e);
			}
		} else {
			LOGGER.info("Ocorreu um problema ao realizar o upload de " + path);
		}
	}

	@RequestMapping(value="/deleteFile", method= RequestMethod.POST)
	public void handleFileUpload(@RequestParam("path") String path){
		try {
			File file = new File(distributedDatabaseConfiguration.getFolder() + path);
			if(file.delete()){
				broadcastFileService.addPathDeleted(path);
				LOGGER.info("Arquivo " + path + " apagado com sucesso");
			}else{
				LOGGER.info("Arquivo " + path + " não foi apagado");
			}
		} catch (Exception e) {
			LOGGER.error("Ocorreu um erro ao apagar o arquivo " + path, e);
		}

	}

}
