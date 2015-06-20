package br.feevale.distributeddatabase.services;

import br.feevale.distributeddatabase.config.DistributedDatabaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.nio.file.*;
import java.util.List;

/**
 * Created by jonasflesch on 5/26/15.
 */
@Service
public class DirectoryWatchService {

	private static final Logger LOG = LoggerFactory.getLogger(DirectoryWatchService.class);

	private final DistributedDatabaseConfiguration distributedDatabaseConfiguration;

	@Autowired
	private BroadcastFileService broadcastFileService;

	@Inject
	public DirectoryWatchService(DistributedDatabaseConfiguration distributedDatabaseConfiguration){
		this.distributedDatabaseConfiguration = distributedDatabaseConfiguration;
	}

	@PostConstruct
	public void watchDirectory(){
		new Thread(){
			@Override
			public void run() {
				try {
					WatchService watchService = FileSystems.getDefault().newWatchService();

					Path directory = Paths.get(distributedDatabaseConfiguration.getFolder());

					directory.register(watchService,
							StandardWatchEventKinds.ENTRY_CREATE,
							StandardWatchEventKinds.ENTRY_DELETE,
							StandardWatchEventKinds.ENTRY_MODIFY);

					while(true){
						WatchKey watchKey = watchService.take();

						List<WatchEvent<?>> keys = watchKey.pollEvents();
						for (WatchEvent<?> watchEvent : keys) {
							// get the kind of event
							WatchEvent.Kind<?> watchEventKind = watchEvent.kind();
							// sometimes events are created faster than they are registered
							// or the implementation
							// may specify a maximum number of events and further events are
							// discarded. In these cases
							// an event of kind overflow is returned. We ignore this case
							// for nowl
							if (watchEventKind == StandardWatchEventKinds.OVERFLOW) {
								continue;
							}
							if (watchEventKind == StandardWatchEventKinds.ENTRY_CREATE) {
								// a new file has been created
								// print the name of the file. To test this, go to the temp
								// directory
								// and create a plain text file. name the file a.txt. If you
								// are on windows, watch what happens!
								System.out.println("File Created:" + watchEvent.context());
								broadcastFileService.createFile(watchEventToPath(watchEvent), watchEventToFile(watchEvent));
							} else if (watchEventKind == StandardWatchEventKinds.ENTRY_MODIFY) {
								// The file has been modified. Go to the file created above
								// and modify it
								System.out.println("File Modified:" + watchEvent.context());
								broadcastFileService.createFile(watchEventToPath(watchEvent), watchEventToFile(watchEvent));
							} else if (watchEventKind == StandardWatchEventKinds.ENTRY_DELETE) {
								// the file has been deleted. delete the file. and exit the
								// loop.
								System.out.println("File deleted:" + watchEvent.context());
								broadcastFileService.deleteFile(watchEventToPath(watchEvent));
							}
							// we need to reset the key so the further key events may be
							// polled
							watchKey.reset();
						}
					}

				} catch (Exception e){
					LOG.error("Error watching directory", e);
				}
			}
		}.start();

	}

	private String watchEventToPath(WatchEvent<?> watchEvent){
		return watchEvent.context().toString();
	}

	private File watchEventToFile(WatchEvent<?> watchEvent){
		return new File(distributedDatabaseConfiguration.getFolder() + watchEvent.context());
	}
}
