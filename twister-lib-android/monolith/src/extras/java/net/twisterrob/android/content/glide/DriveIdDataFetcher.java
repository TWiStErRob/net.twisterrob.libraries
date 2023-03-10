package net.twisterrob.android.content.glide;

import java.io.InputStream;

import org.slf4j.*;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.*;

import static net.twisterrob.android.utils.tools.DriveTools.ContentsUtils.*;

public class DriveIdDataFetcher implements DataFetcher<InputStream> {
	private static final Logger LOG = LoggerFactory.getLogger(DriveIdDataFetcher.class);

	private final GoogleApiClient client;
	private final DriveId driveId;

	private boolean cancelled = false;

	private DriveContents contents;

	public DriveIdDataFetcher(GoogleApiClient client, DriveId driveId) {
		this.client = client;
		this.driveId = driveId;
	}

	public String getId() {
		return driveId.encodeToString();
	}

	public InputStream loadData(Priority priority) {
		if (client == null) {
			LOG.warn("No connected client received, giving custom error image");
			return null;
		}
		DriveFile file = Drive.DriveApi.getFile(client, driveId);
		if (cancelled) {
			return null;
		}
		contents = sync(file.open(client, DriveFile.MODE_READ_ONLY, null));
		if (cancelled) {
			return null;
		}
		return contents.getInputStream();
	}

	public void cancel() {
		cancelled = true;
		if (contents != null) {
			contents.discard(client);
		}
	}

	public void cleanup() {
		if (contents != null) {
			contents.discard(client);
		}
	}
}
