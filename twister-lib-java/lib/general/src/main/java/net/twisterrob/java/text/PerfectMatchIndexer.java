package net.twisterrob.java.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

public class PerfectMatchIndexer<T> implements Indexer<T> {
	/** word -> { (id, keyword) } */
	private final Map<CharSequence, Collection<T>> index = new HashMap<>();

	@Override public @Nonnull Collection<MatchResult<T>> match(@Nonnull CharSequence input) {
		Collection<MatchResult<T>> suggestions = new ArrayList<>();
		CharSequence word = clean(input);
		Collection<T> categories = index.get(word);
		if (categories != null) {
			for (T index : categories) {
				suggestions.add(new MatchResult<>(input, word, 0, index, ""));
			}
		}
		return suggestions;
	}

	@Override public void add(CharSequence word, T entry) {
		word = clean(word);
		Collection<T> wordSuggestions = index.get(word);
		if (wordSuggestions == null) {
			wordSuggestions = new ArrayList<>();
			index.put(word, wordSuggestions);
		}
		wordSuggestions.add(entry);
	}

	@Override public int size() {
		int size = 0;
		for (Collection<T> match : index.values()) {
			size += match.size();
		}
		return size;
	}

	private CharSequence clean(CharSequence word) {
		return word.toString().toLowerCase(Locale.getDefault());
	}
}
