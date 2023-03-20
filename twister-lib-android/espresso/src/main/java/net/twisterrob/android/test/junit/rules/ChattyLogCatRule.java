package net.twisterrob.android.test.junit.rules;

import org.junit.rules.ExternalResource;

import androidx.annotation.NonNull;

import net.twisterrob.android.test.ChattyLogCat;

public class ChattyLogCatRule extends ExternalResource {

	private final @NonNull ChattyLogCat chatty;

	public ChattyLogCatRule() {
		this(new ChattyLogCat());
	}

	public ChattyLogCatRule(@NonNull ChattyLogCat chatty) {
		this.chatty = chatty;
	}

	@Override protected void before() {
		chatty.saveBlackWhiteList();
		chatty.iAmNotChatty();
	}

	@Override protected void after() {
		chatty.restoreLastBlackWhiteList();
	}
}
