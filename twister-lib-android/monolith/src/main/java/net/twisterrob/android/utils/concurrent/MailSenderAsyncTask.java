package net.twisterrob.android.utils.concurrent;

import org.slf4j.*;

/**
 * @deprecated this doesn't belong in here, move to BLT.
 */
@SuppressWarnings("deprecation")
@Deprecated
public class MailSenderAsyncTask extends android.os.AsyncTask<String, Void, Boolean> {
	private static final Logger LOG = LoggerFactory.getLogger(MailSenderAsyncTask.class);

	private final net.twisterrob.java.io.MailSender m = new net.twisterrob.java.io.MailSender();

	public MailSenderAsyncTask(String subject, String from, String... to) {
		m.setTo(to);
		m.setFrom(from);
		m.setSubject(subject);
	}

	@Override protected Boolean doInBackground(String... params) {
		try {
			m.setBody(params[0]);
			m.send();
			return true;
		} catch (Exception ex) {
			LOG.error("Cannot send {}.", m, ex);
			return false;
		}
	}
}
