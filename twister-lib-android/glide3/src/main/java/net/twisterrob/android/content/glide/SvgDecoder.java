package net.twisterrob.android.content.glide;

import java.io.IOException;
import java.io.InputStream;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

/**
 * Decodes an {@link SVG} internal representation from an {@link InputStream}.
 */
public class SvgDecoder implements ResourceDecoder<InputStream, SVG> {
	public Resource<SVG> decode(InputStream source, int width, int height) throws IOException {
		try {
			SVG svg = SVG.getFromInputStream(source);
			return new SimpleResource<>(svg);
		} catch (SVGParseException ex) {
			throw new IOException("Cannot load SVG from stream", ex);
		}
	}

	@Override public String getId() {
		return "";
	}
}
